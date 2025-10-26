package com.cms.backend.repository

import com.cms.backend.entity.BlogPost
import com.cms.backend.entity.PostStatus
import com.cms.backend.entity.Tag
import com.cms.backend.entity.User
import com.cms.backend.entity.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@ActiveProfiles("test")
class BlogPostRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var blogPostRepository: BlogPostRepository

    private lateinit var testAuthor: User

    @BeforeEach
    fun setup() {
        testAuthor = User(
            email = "author@example.com",
            password = "password",
            firstName = "Test",
            lastName = "Author",
            username = "testauthor",
            role = UserRole.AUTHOR
        )
        entityManager.persist(testAuthor)
        entityManager.flush()
    }

    @Test
    fun `should find blog post by slug`() {
        // Given
        val blogPost = BlogPost(
            title = "Test Post",
            slug = "test-post",
            mdxContent = "# Content",
            author = testAuthor,
            status = PostStatus.PUBLISHED
        )
        entityManager.persist(blogPost)
        entityManager.flush()

        // When
        val found = blogPostRepository.findBySlug("test-post")

        // Then
        assertThat(found).isPresent
        assertThat(found.get().title).isEqualTo("Test Post")
    }

    @Test
    fun `should return empty when slug not found`() {
        // When
        val found = blogPostRepository.findBySlug("nonexistent-slug")

        // Then
        assertThat(found).isEmpty
    }

    @Test
    fun `should find all posts by status`() {
        // Given
        createBlogPost("Published 1", "published-1", PostStatus.PUBLISHED)
        createBlogPost("Published 2", "published-2", PostStatus.PUBLISHED)
        createBlogPost("Draft", "draft", PostStatus.DRAFT)

        // When
        val publishedPosts = blogPostRepository.findAllByStatus(PostStatus.PUBLISHED, PageRequest.of(0, 10))

        // Then
        assertThat(publishedPosts.content).hasSize(2)
        assertThat(publishedPosts.content.map { it.slug }).containsExactlyInAnyOrder("published-1", "published-2")
    }

    @Test
    fun `should find posts by author`() {
        // Given
        val anotherAuthor = User(
            email = "another@example.com",
            password = "password",
            firstName = "Another",
            lastName = "Author",
            username = "another",
            role = UserRole.AUTHOR
        )
        entityManager.persist(anotherAuthor)

        createBlogPost("Post by test author", "test-author-post", PostStatus.PUBLISHED, testAuthor)
        createBlogPost("Another post by test author", "test-author-post-2", PostStatus.PUBLISHED, testAuthor)
        createBlogPost("Post by another author", "another-author-post", PostStatus.PUBLISHED, anotherAuthor)

        // When
        val posts = blogPostRepository.findAllByAuthorId(testAuthor.id!!, PageRequest.of(0, 10))

        // Then
        assertThat(posts.content).hasSize(2)
        assertThat(posts.content.map { it.author.username }).allMatch { it == "testauthor" }
    }

    @Test
    fun `should find featured posts`() {
        // Given
        val featuredPost = BlogPost(
            title = "Featured Post",
            slug = "featured-post",
            mdxContent = "# Content",
            author = testAuthor,
            status = PostStatus.PUBLISHED,
            featured = true
        )
        val normalPost = BlogPost(
            title = "Normal Post",
            slug = "normal-post",
            mdxContent = "# Content",
            author = testAuthor,
            status = PostStatus.PUBLISHED,
            featured = false
        )
        entityManager.persist(featuredPost)
        entityManager.persist(normalPost)
        entityManager.flush()

        // When
        val featured = blogPostRepository.findAllByFeaturedTrue(PageRequest.of(0, 10))

        // Then
        assertThat(featured.content).hasSize(1)
        assertThat(featured.content[0].slug).isEqualTo("featured-post")
    }

    @Test
    fun `should find published posts ordered by publishedAt`() {
        // Given
        val older = BlogPost(
            title = "Older Post",
            slug = "older-post",
            mdxContent = "# Content",
            author = testAuthor,
            status = PostStatus.PUBLISHED,
            publishedAt = LocalDateTime.now().minusDays(5)
        )
        val newer = BlogPost(
            title = "Newer Post",
            slug = "newer-post",
            mdxContent = "# Content",
            author = testAuthor,
            status = PostStatus.PUBLISHED,
            publishedAt = LocalDateTime.now().minusDays(1)
        )
        entityManager.persist(older)
        entityManager.persist(newer)
        entityManager.flush()

        // When
        val published = blogPostRepository.findPublishedPosts(PostStatus.PUBLISHED, PageRequest.of(0, 10))

        // Then
        assertThat(published.content).hasSize(2)
        // Newer should come first (DESC order)
        assertThat(published.content[0].slug).isEqualTo("newer-post")
        assertThat(published.content[1].slug).isEqualTo("older-post")
    }

    @Test
    fun `should find posts by tag`() {
        // Given
        val tag1 = Tag(name = "Kotlin", slug = "kotlin")
        val tag2 = Tag(name = "Java", slug = "java")
        entityManager.persist(tag1)
        entityManager.persist(tag2)

        val post1 = BlogPost(
            title = "Kotlin Post",
            slug = "kotlin-post",
            mdxContent = "# Content",
            author = testAuthor,
            status = PostStatus.PUBLISHED,
            tags = mutableListOf(tag1)
        )
        val post2 = BlogPost(
            title = "Java Post",
            slug = "java-post",
            mdxContent = "# Content",
            author = testAuthor,
            status = PostStatus.PUBLISHED,
            tags = mutableListOf(tag2)
        )
        entityManager.persist(post1)
        entityManager.persist(post2)
        entityManager.flush()

        // When
        val kotlinPosts = blogPostRepository.findByTagIdAndStatus(tag1.id!!, PostStatus.PUBLISHED, PageRequest.of(0, 10))

        // Then
        assertThat(kotlinPosts.content).hasSize(1)
        assertThat(kotlinPosts.content[0].slug).isEqualTo("kotlin-post")
    }

    @Test
    fun `should search posts by title`() {
        // Given
        createBlogPost("Understanding Kotlin Coroutines", "kotlin-coroutines", PostStatus.PUBLISHED)
        createBlogPost("Java Spring Boot Tutorial", "java-spring", PostStatus.PUBLISHED)
        createBlogPost("Advanced Kotlin Tips", "kotlin-tips", PostStatus.PUBLISHED)

        // When
        val results = blogPostRepository.searchByTitleOrExcerpt("Kotlin", PostStatus.PUBLISHED, PageRequest.of(0, 10))

        // Then
        assertThat(results.content).hasSize(2)
        assertThat(results.content.map { it.title }).allMatch { it.contains("Kotlin", ignoreCase = true) }
    }

    @Test
    fun `should search posts by excerpt`() {
        // Given
        val post = BlogPost(
            title = "Some Title",
            slug = "some-title",
            excerpt = "This post talks about TypeScript features",
            mdxContent = "# Content",
            author = testAuthor,
            status = PostStatus.PUBLISHED
        )
        entityManager.persist(post)
        entityManager.flush()

        // When
        val results = blogPostRepository.searchByTitleOrExcerpt("TypeScript", PostStatus.PUBLISHED, PageRequest.of(0, 10))

        // Then
        assertThat(results.content).hasSize(1)
        assertThat(results.content[0].excerpt).contains("TypeScript")
    }

    @Test
    fun `should save blog post with all fields`() {
        // Given
        val blogPost = BlogPost(
            title = "Complete Post",
            slug = "complete-post",
            excerpt = "This is an excerpt",
            mdxContent = "# Full Content",
            featuredImageUrl = "https://example.com/image.jpg",
            author = testAuthor,
            status = PostStatus.PUBLISHED,
            metaTitle = "Meta Title",
            metaDescription = "Meta Description",
            metaKeywords = "kotlin, spring",
            publishedAt = LocalDateTime.now(),
            readingTimeMinutes = 5,
            allowComments = true,
            featured = true,
            viewCount = 100
        )

        // When
        val saved = blogPostRepository.save(blogPost)
        val found = blogPostRepository.findById(saved.id!!).get()

        // Then
        assertThat(found.title).isEqualTo("Complete Post")
        assertThat(found.excerpt).isEqualTo("This is an excerpt")
        assertThat(found.metaTitle).isEqualTo("Meta Title")
        assertThat(found.readingTimeMinutes).isEqualTo(5)
        assertThat(found.viewCount).isEqualTo(100)
        assertThat(found.createdAt).isNotNull
        assertThat(found.updatedAt).isNotNull
    }

    private fun createBlogPost(
        title: String,
        slug: String,
        status: PostStatus,
        author: User = testAuthor
    ): BlogPost {
        val post = BlogPost(
            title = title,
            slug = slug,
            mdxContent = "# Content",
            author = author,
            status = status
        )
        entityManager.persist(post)
        entityManager.flush()
        return post
    }
}

