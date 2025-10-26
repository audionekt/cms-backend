package com.cms.backend.service

import com.cms.backend.dto.CreateBlogPostRequest
import com.cms.backend.dto.UpdateBlogPostRequest
import com.cms.backend.entity.BlogPost
import com.cms.backend.entity.PostStatus
import com.cms.backend.entity.Tag
import com.cms.backend.entity.User
import com.cms.backend.entity.UserRole
import com.cms.backend.exception.ResourceNotFoundException
import com.cms.backend.repository.BlogPostRepository
import com.cms.backend.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.util.*

class BlogPostServiceTest {

    private val blogPostRepository: BlogPostRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val tagService: TagService = mockk()
    private val mediaService: MediaService = mockk()
    private val blogPostService = BlogPostService(blogPostRepository, userRepository, tagService, mediaService)

    @Test
    fun `should create blog post successfully`() {
        // Given
        val request = CreateBlogPostRequest(
            title = "Test Post",
            slug = "test-post",
            mdxContent = "# Content",
            status = PostStatus.DRAFT,
            tagIds = listOf(1L)
        )

        val author = User(
            id = 1L,
            email = "author@example.com",
            password = "password",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            role = UserRole.AUTHOR
        )

        val tag = Tag(id = 1L, name = "Kotlin", slug = "kotlin")

        every { blogPostRepository.findBySlug("test-post") } returns Optional.empty()
        every { userRepository.findById(1L) } returns Optional.of(author)
        every { tagService.findTagById(1L) } returns tag

        val postSlot = slot<BlogPost>()
        every { blogPostRepository.save(capture(postSlot)) } answers { postSlot.captured.copy(id = 1L) }

        // When
        val result = blogPostService.createBlogPost(request, 1L)

        // Then
        assertThat(result.title).isEqualTo("Test Post")
        assertThat(result.slug).isEqualTo("test-post")
        assertThat(result.status).isEqualTo(PostStatus.DRAFT)
        assertThat(result.tags).hasSize(1)
        verify(exactly = 1) { blogPostRepository.save(any()) }
    }


    @Test
    fun `should get blog post by id`() {
        // Given
        val blogPost = createTestBlogPost(1L, "Test Post", "test-post")

        every { blogPostRepository.findById(1L) } returns Optional.of(blogPost)

        // When
        val result = blogPostService.getBlogPostById(1L)

        // Then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("Test Post")
    }

    @Test
    fun `should throw exception when blog post not found`() {
        // Given
        every { blogPostRepository.findById(999L) } returns Optional.empty()

        // When & Then
        assertThatThrownBy { blogPostService.getBlogPostById(999L) }
            .isInstanceOf(ResourceNotFoundException::class.java)
            .hasMessageContaining("Blog post with id 999 not found")
    }

    @Test
    fun `should get blog post by slug`() {
        // Given
        val blogPost = createTestBlogPost(1L, "Test Post", "test-post")

        every { blogPostRepository.findBySlug("test-post") } returns Optional.of(blogPost)

        // When
        val result = blogPostService.getBlogPostBySlug("test-post")

        // Then
        assertThat(result.slug).isEqualTo("test-post")
    }

    @Test
    fun `should get all blog posts paginated`() {
        // Given
        val posts = listOf(
            createTestBlogPost(1L, "Post 1", "post-1"),
            createTestBlogPost(2L, "Post 2", "post-2")
        )

        val pageable = PageRequest.of(0, 10)
        every { blogPostRepository.findAll(pageable) } returns PageImpl(posts)

        // When
        val result = blogPostService.getAllBlogPosts(pageable)

        // Then
        assertThat(result.content).hasSize(2)
        assertThat(result.totalElements).isEqualTo(2)
    }

    @Test
    fun `should get published blog posts`() {
        // Given
        val posts = listOf(createTestBlogPost(1L, "Published Post", "published-post", PostStatus.PUBLISHED))

        val pageable = PageRequest.of(0, 10)
        every { blogPostRepository.findPublishedPosts(PostStatus.PUBLISHED, pageable) } returns PageImpl(posts)

        // When
        val result = blogPostService.getPublishedBlogPosts(pageable)

        // Then
        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].status).isEqualTo(PostStatus.PUBLISHED)
    }

    @Test
    fun `should get featured blog posts`() {
        // Given
        val posts = listOf(createTestBlogPost(1L, "Featured", "featured", featured = true))

        val pageable = PageRequest.of(0, 10)
        every { blogPostRepository.findAllByFeaturedTrue(pageable) } returns PageImpl(posts)

        // When
        val result = blogPostService.getFeaturedBlogPosts(pageable)

        // Then
        assertThat(result.content).hasSize(1)
    }

    @Test
    fun `should search blog posts`() {
        // Given
        val posts = listOf(createTestBlogPost(1L, "Kotlin Tutorial", "kotlin-tutorial"))

        val pageable = PageRequest.of(0, 10)
        every { blogPostRepository.searchByTitleOrExcerpt("Kotlin", PostStatus.PUBLISHED, pageable) } returns PageImpl(posts)

        // When
        val result = blogPostService.searchBlogPosts("Kotlin", pageable)

        // Then
        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].title).contains("Kotlin")
    }

    @Test
    fun `should get blog posts by tag`() {
        // Given
        val posts = listOf(createTestBlogPost(1L, "Tagged Post", "tagged-post"))

        val pageable = PageRequest.of(0, 10)
        every { blogPostRepository.findByTagIdAndStatus(1L, PostStatus.PUBLISHED, pageable) } returns PageImpl(posts)

        // When
        val result = blogPostService.getBlogPostsByTag(1L, pageable)

        // Then
        assertThat(result.content).hasSize(1)
    }

    @Test
    fun `should update blog post successfully`() {
        // Given
        val existingPost = createTestBlogPost(1L, "Old Title", "old-slug")
        val updateRequest = UpdateBlogPostRequest(
            title = "New Title",
            mdxContent = "# Updated content"
        )

        every { blogPostRepository.findById(1L) } returns Optional.of(existingPost)
        every { blogPostRepository.findBySlug(any()) } returns Optional.empty()

        val updatedPostSlot = slot<BlogPost>()
        every { blogPostRepository.save(capture(updatedPostSlot)) } answers { updatedPostSlot.captured }

        // When
        val result = blogPostService.updateBlogPost(1L, updateRequest)

        // Then
        assertThat(result.title).isEqualTo("New Title")
        assertThat(updatedPostSlot.captured.mdxContent).isEqualTo("# Updated content")
    }

    @Test
    fun `should increment view count`() {
        // Given
        val blogPost = createTestBlogPost(1L, "Test", "test", viewCount = 5)

        every { blogPostRepository.findById(1L) } returns Optional.of(blogPost)

        val updatedPostSlot = slot<BlogPost>()
        every { blogPostRepository.save(capture(updatedPostSlot)) } answers { updatedPostSlot.captured }

        // When
        blogPostService.incrementViewCount(1L)

        // Then
        assertThat(updatedPostSlot.captured.viewCount).isEqualTo(6)
    }

    @Test
    fun `should delete blog post`() {
        // Given
        val blogPost = createTestBlogPost(1L, "Test", "test")

        every { blogPostRepository.findById(1L) } returns Optional.of(blogPost)
        every { blogPostRepository.delete(blogPost) } returns Unit

        // When
        blogPostService.deleteBlogPost(1L)

        // Then
        verify(exactly = 1) { blogPostRepository.delete(blogPost) }
    }

    @Test
    fun `should get blog posts by status`() {
        // Given
        val posts = listOf(createTestBlogPost(1L, "Draft Post", "draft-post", PostStatus.DRAFT))

        val pageable = PageRequest.of(0, 10)
        every { blogPostRepository.findAllByStatus(PostStatus.DRAFT, pageable) } returns PageImpl(posts)

        // When
        val result = blogPostService.getBlogPostsByStatus(PostStatus.DRAFT, pageable)

        // Then
        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].status).isEqualTo(PostStatus.DRAFT)
    }

    @Test
    fun `should get blog posts by author`() {
        // Given
        val posts = listOf(createTestBlogPost(1L, "Author Post", "author-post"))

        val pageable = PageRequest.of(0, 10)
        every { blogPostRepository.findAllByAuthorId(1L, pageable) } returns PageImpl(posts)

        // When
        val result = blogPostService.getBlogPostsByAuthor(1L, pageable)

        // Then
        assertThat(result.content).hasSize(1)
    }

    private fun createTestBlogPost(
        id: Long,
        title: String,
        slug: String,
        status: PostStatus = PostStatus.DRAFT,
        featured: Boolean = false,
        viewCount: Long = 0L
    ): BlogPost {
        return BlogPost(
            id = id,
            title = title,
            slug = slug,
            mdxContent = "# Content",
            author = User(
                id = 1L,
                email = "author@example.com",
                password = "password",
                firstName = "John",
                lastName = "Doe",
                username = "johndoe",
                role = UserRole.AUTHOR
            ),
            status = status,
            featured = featured,
            viewCount = viewCount,
            publishedAt = if (status == PostStatus.PUBLISHED) LocalDateTime.now() else null
        )
    }
}

