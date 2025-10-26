package com.cms.backend.service

import com.cms.backend.dto.*
import com.cms.backend.entity.BlogPost
import com.cms.backend.entity.PostStatus
import com.cms.backend.exception.ResourceNotFoundException
import com.cms.backend.repository.BlogPostRepository
import com.cms.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class BlogPostService(
    private val blogPostRepository: BlogPostRepository,
    private val userRepository: UserRepository,
    private val tagService: TagService,
    private val mediaService: MediaService
) {
    private val logger = LoggerFactory.getLogger(BlogPostService::class.java)

    fun createBlogPost(request: CreateBlogPostRequest, authorId: Long): BlogPostResponse {
        logger.info("Creating blog post with title: ${request.title}")

        val author = userRepository.findById(authorId)
            .orElseThrow { ResourceNotFoundException("Author with id $authorId not found") }

        val tags = request.tagIds.map { tagService.findTagById(it) }.toMutableList()
        val featuredMedia = request.featuredMediaId?.let { mediaService.findMediaById(it) }

        val blogPost = BlogPost(
            title = request.title,
            slug = request.slug,
            excerpt = request.excerpt,
            mdxContent = request.mdxContent,
            featuredImageUrl = request.featuredImageUrl,
            featuredMedia = featuredMedia,
            author = author,
            tags = tags,
            status = request.status,
            metaTitle = request.metaTitle,
            metaDescription = request.metaDescription,
            metaKeywords = request.metaKeywords,
            scheduledAt = request.scheduledAt,
            readingTimeMinutes = request.readingTimeMinutes ?: calculateReadingTime(request.mdxContent),
            allowComments = request.allowComments,
            featured = request.featured,
            publishedAt = if (request.status == PostStatus.PUBLISHED) LocalDateTime.now() else null
        )

        val savedPost = blogPostRepository.save(blogPost)
        logger.info("Blog post created successfully with id: ${savedPost.id}")
        return BlogPostResponse.fromEntity(savedPost)
    }

    @Transactional(readOnly = true)
    fun getBlogPostById(id: Long): BlogPostResponse {
        val blogPost = findBlogPostById(id)
        return BlogPostResponse.fromEntity(blogPost)
    }

    @Transactional(readOnly = true)
    fun getBlogPostBySlug(slug: String): BlogPostResponse {
        val blogPost = blogPostRepository.findBySlug(slug)
            .orElseThrow { ResourceNotFoundException("Blog post with slug $slug not found") }
        return BlogPostResponse.fromEntity(blogPost)
    }

    @Transactional(readOnly = true)
    fun getAllBlogPosts(pageable: Pageable): PageResponse<BlogPostSummaryResponse> {
        val page = blogPostRepository.findAll(pageable)
        return PageResponse.fromPage(page) { BlogPostSummaryResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getBlogPostsByStatus(status: PostStatus, pageable: Pageable): PageResponse<BlogPostSummaryResponse> {
        val page = blogPostRepository.findAllByStatus(status, pageable)
        return PageResponse.fromPage(page) { BlogPostSummaryResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getPublishedBlogPosts(pageable: Pageable): PageResponse<BlogPostSummaryResponse> {
        val page = blogPostRepository.findPublishedPosts(PostStatus.PUBLISHED, pageable)
        return PageResponse.fromPage(page) { BlogPostSummaryResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getBlogPostsByAuthor(authorId: Long, pageable: Pageable): PageResponse<BlogPostSummaryResponse> {
        val page = blogPostRepository.findAllByAuthorId(authorId, pageable)
        return PageResponse.fromPage(page) { BlogPostSummaryResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getBlogPostsByTag(tagId: Long, pageable: Pageable): PageResponse<BlogPostSummaryResponse> {
        val page = blogPostRepository.findByTagIdAndStatus(tagId, PostStatus.PUBLISHED, pageable)
        return PageResponse.fromPage(page) { BlogPostSummaryResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getFeaturedBlogPosts(pageable: Pageable): PageResponse<BlogPostSummaryResponse> {
        val page = blogPostRepository.findAllByFeaturedTrue(pageable)
        return PageResponse.fromPage(page) { BlogPostSummaryResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun searchBlogPosts(searchTerm: String, pageable: Pageable): PageResponse<BlogPostSummaryResponse> {
        val page = blogPostRepository.searchByTitleOrExcerpt(searchTerm, PostStatus.PUBLISHED, pageable)
        return PageResponse.fromPage(page) { BlogPostSummaryResponse.fromEntity(it) }
    }

    fun updateBlogPost(id: Long, request: UpdateBlogPostRequest): BlogPostResponse {
        logger.info("Updating blog post with id: $id")
        val blogPost = findBlogPostById(id)

        val tags = request.tagIds?.map { tagService.findTagById(it) }?.toMutableList() 
            ?: blogPost.tags
        val featuredMedia = request.featuredMediaId?.let { mediaService.findMediaById(it) } 
            ?: blogPost.featuredMedia

        // If status is being changed to PUBLISHED and it wasn't published before, set publishedAt
        val publishedAt = when {
            request.status == PostStatus.PUBLISHED && blogPost.publishedAt == null -> LocalDateTime.now()
            request.status != PostStatus.PUBLISHED -> null
            else -> blogPost.publishedAt
        }

        val updatedBlogPost = blogPost.copy(
            title = request.title ?: blogPost.title,
            slug = request.slug ?: blogPost.slug,
            excerpt = request.excerpt ?: blogPost.excerpt,
            mdxContent = request.mdxContent ?: blogPost.mdxContent,
            featuredImageUrl = request.featuredImageUrl ?: blogPost.featuredImageUrl,
            featuredMedia = featuredMedia,
            tags = tags,
            status = request.status ?: blogPost.status,
            metaTitle = request.metaTitle ?: blogPost.metaTitle,
            metaDescription = request.metaDescription ?: blogPost.metaDescription,
            metaKeywords = request.metaKeywords ?: blogPost.metaKeywords,
            scheduledAt = request.scheduledAt ?: blogPost.scheduledAt,
            readingTimeMinutes = request.readingTimeMinutes ?: blogPost.readingTimeMinutes,
            allowComments = request.allowComments ?: blogPost.allowComments,
            featured = request.featured ?: blogPost.featured,
            publishedAt = publishedAt
        )

        val savedPost = blogPostRepository.save(updatedBlogPost)
        logger.info("Blog post updated successfully with id: ${savedPost.id}")
        return BlogPostResponse.fromEntity(savedPost)
    }

    fun incrementViewCount(id: Long) {
        val blogPost = findBlogPostById(id)
        val updatedPost = blogPost.copy(viewCount = blogPost.viewCount + 1)
        blogPostRepository.save(updatedPost)
    }

    fun deleteBlogPost(id: Long) {
        logger.info("Deleting blog post with id: $id")
        val blogPost = findBlogPostById(id)
        blogPostRepository.delete(blogPost)
        logger.info("Blog post deleted successfully with id: $id")
    }

    private fun findBlogPostById(id: Long): BlogPost {
        return blogPostRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Blog post with id $id not found") }
    }

    private fun calculateReadingTime(content: String): Int {
        val wordsPerMinute = 200
        val wordCount = content.split("\\s+".toRegex()).size
        return (wordCount / wordsPerMinute).coerceAtLeast(1)
    }
}
