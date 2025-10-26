package com.cms.backend.controller

import com.cms.backend.dto.*
import com.cms.backend.entity.PostStatus
import com.cms.backend.service.BlogPostService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/posts")
class BlogPostController(
    private val blogPostService: BlogPostService
) {

    @PostMapping
    fun createBlogPost(
        @Valid @RequestBody request: CreateBlogPostRequest,
        @RequestParam authorId: Long
    ): ResponseEntity<BlogPostResponse> {
        val blogPost = blogPostService.createBlogPost(request, authorId)
        return ResponseEntity.status(HttpStatus.CREATED).body(blogPost)
    }

    @GetMapping("/{id}")
    fun getBlogPostById(@PathVariable id: Long): ResponseEntity<BlogPostResponse> {
        val blogPost = blogPostService.getBlogPostById(id)
        return ResponseEntity.ok(blogPost)
    }

    @GetMapping("/slug/{slug}")
    fun getBlogPostBySlug(@PathVariable slug: String): ResponseEntity<BlogPostResponse> {
        val blogPost = blogPostService.getBlogPostBySlug(slug)
        return ResponseEntity.ok(blogPost)
    }

    @GetMapping
    fun getAllBlogPosts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "DESC") sortDir: String
    ): ResponseEntity<PageResponse<BlogPostSummaryResponse>> {
        val direction = if (sortDir.uppercase() == "ASC") Sort.Direction.ASC else Sort.Direction.DESC
        val pageable = PageRequest.of(page, size, Sort.by(direction, sortBy))
        val blogPosts = blogPostService.getAllBlogPosts(pageable)
        return ResponseEntity.ok(blogPosts)
    }

    @GetMapping("/status/{status}")
    fun getBlogPostsByStatus(
        @PathVariable status: PostStatus,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PageResponse<BlogPostSummaryResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val blogPosts = blogPostService.getBlogPostsByStatus(status, pageable)
        return ResponseEntity.ok(blogPosts)
    }

    @GetMapping("/published")
    fun getPublishedBlogPosts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PageResponse<BlogPostSummaryResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"))
        val blogPosts = blogPostService.getPublishedBlogPosts(pageable)
        return ResponseEntity.ok(blogPosts)
    }

    @GetMapping("/author/{authorId}")
    fun getBlogPostsByAuthor(
        @PathVariable authorId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PageResponse<BlogPostSummaryResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val blogPosts = blogPostService.getBlogPostsByAuthor(authorId, pageable)
        return ResponseEntity.ok(blogPosts)
    }

    @GetMapping("/tag/{tagId}")
    fun getBlogPostsByTag(
        @PathVariable tagId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PageResponse<BlogPostSummaryResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"))
        val blogPosts = blogPostService.getBlogPostsByTag(tagId, pageable)
        return ResponseEntity.ok(blogPosts)
    }

    @GetMapping("/featured")
    fun getFeaturedBlogPosts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PageResponse<BlogPostSummaryResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"))
        val blogPosts = blogPostService.getFeaturedBlogPosts(pageable)
        return ResponseEntity.ok(blogPosts)
    }

    @GetMapping("/search")
    fun searchBlogPosts(
        @RequestParam searchTerm: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PageResponse<BlogPostSummaryResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"))
        val blogPosts = blogPostService.searchBlogPosts(searchTerm, pageable)
        return ResponseEntity.ok(blogPosts)
    }

    @PutMapping("/{id}")
    fun updateBlogPost(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateBlogPostRequest
    ): ResponseEntity<BlogPostResponse> {
        val blogPost = blogPostService.updateBlogPost(id, request)
        return ResponseEntity.ok(blogPost)
    }

    @PostMapping("/{id}/view")
    fun incrementViewCount(@PathVariable id: Long): ResponseEntity<Void> {
        blogPostService.incrementViewCount(id)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    fun deleteBlogPost(@PathVariable id: Long): ResponseEntity<Void> {
        blogPostService.deleteBlogPost(id)
        return ResponseEntity.noContent().build()
    }
}
