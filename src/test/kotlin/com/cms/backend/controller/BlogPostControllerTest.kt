package com.cms.backend.controller

import com.cms.backend.dto.BlogPostResponse
import com.cms.backend.dto.BlogPostSummaryResponse
import com.cms.backend.dto.CreateBlogPostRequest
import com.cms.backend.dto.PageResponse
import com.cms.backend.dto.TagSummaryResponse
import com.cms.backend.dto.UserSummaryResponse
import com.cms.backend.entity.PostStatus
import com.cms.backend.service.BlogPostService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(BlogPostController::class)
@ActiveProfiles("test")
@WithMockUser
class BlogPostControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var blogPostService: BlogPostService

    @Test
    fun `should create blog post successfully`() {
        // Given
        val request = CreateBlogPostRequest(
            title = "Test Post",
            slug = "test-post",
            mdxContent = "# Content",
            status = PostStatus.DRAFT
        )

        val response = BlogPostResponse(
            id = 1L,
            title = "Test Post",
            slug = "test-post",
            excerpt = null,
            mdxContent = "# Content",
            featuredImageUrl = null,
            featuredMedia = null,
            author = UserSummaryResponse(1L, "testuser", "Test", "User", null),
            tags = emptyList(),
            status = PostStatus.DRAFT,
            metaTitle = null,
            metaDescription = null,
            metaKeywords = null,
            publishedAt = null,
            scheduledAt = null,
            viewCount = 0,
            readingTimeMinutes = null,
            allowComments = true,
            featured = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { blogPostService.createBlogPost(any(), 1L) } returns response

        // When & Then
        mockMvc.perform(
            post("/api/v1/posts")
                .with(csrf())
                .param("authorId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Post"))
            .andExpect(jsonPath("$.slug").value("test-post"))

        verify(exactly = 1) { blogPostService.createBlogPost(any(), 1L) }
    }

    @Test
    fun `should get blog post by id`() {
        // Given
        val response = BlogPostResponse(
            id = 1L,
            title = "Test Post",
            slug = "test-post",
            excerpt = "Excerpt",
            mdxContent = "# Content",
            featuredImageUrl = null,
            featuredMedia = null,
            author = UserSummaryResponse(1L, "testuser", "Test", "User", null),
            tags = listOf(TagSummaryResponse(1L, "Kotlin", "kotlin")),
            status = PostStatus.PUBLISHED,
            metaTitle = null,
            metaDescription = null,
            metaKeywords = null,
            publishedAt = LocalDateTime.now(),
            scheduledAt = null,
            viewCount = 10,
            readingTimeMinutes = 5,
            allowComments = true,
            featured = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { blogPostService.getBlogPostById(1L) } returns response

        // When & Then
        mockMvc.perform(get("/api/v1/posts/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Post"))
            .andExpect(jsonPath("$.viewCount").value(10))
            .andExpect(jsonPath("$.tags[0].name").value("Kotlin"))
    }

    @Test
    fun `should get blog post by slug`() {
        // Given
        val response = BlogPostResponse(
            id = 1L,
            title = "Test Post",
            slug = "test-post",
            excerpt = null,
            mdxContent = "# Content",
            featuredImageUrl = null,
            featuredMedia = null,
            author = UserSummaryResponse(1L, "testuser", "Test", "User", null),
            tags = emptyList(),
            status = PostStatus.PUBLISHED,
            metaTitle = null,
            metaDescription = null,
            metaKeywords = null,
            publishedAt = LocalDateTime.now(),
            scheduledAt = null,
            viewCount = 0,
            readingTimeMinutes = null,
            allowComments = true,
            featured = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { blogPostService.getBlogPostBySlug("test-post") } returns response

        // When & Then
        mockMvc.perform(get("/api/v1/posts/slug/test-post"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.slug").value("test-post"))
    }

    @Test
    fun `should get all blog posts with pagination`() {
        // Given
        val summaries = listOf(
            createSummaryResponse(1L, "Post 1", "post-1"),
            createSummaryResponse(2L, "Post 2", "post-2")
        )

        val pageResponse = PageResponse(
            content = summaries,
            page = 0,
            size = 10,
            totalElements = 2,
            totalPages = 1,
            last = true
        )

        every { blogPostService.getAllBlogPosts(any()) } returns pageResponse

        // When & Then
        mockMvc.perform(get("/api/v1/posts").param("page", "0").param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.page").value(0))
    }

    @Test
    fun `should get published blog posts`() {
        // Given
        val summaries = listOf(createSummaryResponse(1L, "Published Post", "published-post"))

        val pageResponse = PageResponse(
            content = summaries,
            page = 0,
            size = 10,
            totalElements = 1,
            totalPages = 1,
            last = true
        )

        every { blogPostService.getPublishedBlogPosts(any()) } returns pageResponse

        // When & Then
        mockMvc.perform(get("/api/v1/posts/published"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].title").value("Published Post"))
    }

    @Test
    fun `should get featured blog posts`() {
        // Given
        val summaries = listOf(createSummaryResponse(1L, "Featured Post", "featured-post"))

        val pageResponse = PageResponse(
            content = summaries,
            page = 0,
            size = 10,
            totalElements = 1,
            totalPages = 1,
            last = true
        )

        every { blogPostService.getFeaturedBlogPosts(any()) } returns pageResponse

        // When & Then
        mockMvc.perform(get("/api/v1/posts/featured"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].title").value("Featured Post"))
    }

    @Test
    fun `should search blog posts`() {
        // Given
        val summaries = listOf(createSummaryResponse(1L, "Kotlin Tutorial", "kotlin-tutorial"))

        val pageResponse = PageResponse(
            content = summaries,
            page = 0,
            size = 10,
            totalElements = 1,
            totalPages = 1,
            last = true
        )

        every { blogPostService.searchBlogPosts("Kotlin", any()) } returns pageResponse

        // When & Then
        mockMvc.perform(get("/api/v1/posts/search").param("searchTerm", "Kotlin"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].title").value("Kotlin Tutorial"))
    }

    @Test
    fun `should increment view count`() {
        // Given
        every { blogPostService.incrementViewCount(1L) } returns Unit

        // When & Then
        mockMvc.perform(post("/api/v1/posts/1/view").with(csrf()))
            .andExpect(status().isOk)

        verify(exactly = 1) { blogPostService.incrementViewCount(1L) }
    }

    @Test
    fun `should delete blog post`() {
        // Given
        every { blogPostService.deleteBlogPost(1L) } returns Unit

        // When & Then
        mockMvc.perform(delete("/api/v1/posts/1").with(csrf()))
            .andExpect(status().isNoContent)

        verify(exactly = 1) { blogPostService.deleteBlogPost(1L) }
    }

    @Test
    fun `should return bad request for invalid create request`() {
        // Given
        val invalidRequest = CreateBlogPostRequest(
            title = "ab",  // Too short (min 3)
            slug = "INVALID SLUG",  // Invalid pattern
            mdxContent = "# Content"
        )

        // When & Then
        mockMvc.perform(
            post("/api/v1/posts")
                .with(csrf())
                .param("authorId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
    }

    private fun createSummaryResponse(id: Long, title: String, slug: String): BlogPostSummaryResponse {
        return BlogPostSummaryResponse(
            id = id,
            title = title,
            slug = slug,
            excerpt = null,
            featuredImageUrl = null,
            author = UserSummaryResponse(1L, "testuser", "Test", "User", null),
            tags = emptyList(),
            status = PostStatus.PUBLISHED,
            publishedAt = LocalDateTime.now(),
            viewCount = 0,
            readingTimeMinutes = 5,
            featured = false,
            createdAt = LocalDateTime.now()
        )
    }
}

