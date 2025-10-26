package com.cms.backend.dto

import com.cms.backend.entity.BlogPost
import com.cms.backend.entity.PostStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

// Request DTOs
data class CreateBlogPostRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    val title: String,

    @field:NotBlank(message = "Slug is required")
    @field:Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be lowercase alphanumeric with hyphens")
    @field:Size(min = 3, max = 255, message = "Slug must be between 3 and 255 characters")
    val slug: String,

    @field:Size(max = 500, message = "Excerpt must not exceed 500 characters")
    val excerpt: String? = null,

    @field:NotBlank(message = "MDX content is required")
    val mdxContent: String,

    val featuredImageUrl: String? = null,
    
    @field:Positive(message = "Featured media ID must be positive")
    val featuredMediaId: Long? = null,
    
    val tagIds: List<Long> = emptyList(),
    val status: PostStatus = PostStatus.DRAFT,
    
    @field:Size(max = 60, message = "Meta title should not exceed 60 characters for SEO")
    val metaTitle: String? = null,
    
    @field:Size(max = 160, message = "Meta description should not exceed 160 characters for SEO")
    val metaDescription: String? = null,
    
    val metaKeywords: String? = null,
    val scheduledAt: LocalDateTime? = null,
    
    @field:Positive(message = "Reading time must be positive")
    val readingTimeMinutes: Int? = null,
    
    val allowComments: Boolean = true,
    val featured: Boolean = false
)

data class UpdateBlogPostRequest(
    val title: String? = null,
    val slug: String? = null,
    val excerpt: String? = null,
    val mdxContent: String? = null,
    val featuredImageUrl: String? = null,
    val featuredMediaId: Long? = null,
    val tagIds: List<Long>? = null,
    val status: PostStatus? = null,
    val metaTitle: String? = null,
    val metaDescription: String? = null,
    val metaKeywords: String? = null,
    val scheduledAt: LocalDateTime? = null,
    val readingTimeMinutes: Int? = null,
    val allowComments: Boolean? = null,
    val featured: Boolean? = null
)

// Response DTOs
data class BlogPostResponse(
    val id: Long,
    val title: String,
    val slug: String,
    val excerpt: String?,
    val mdxContent: String,
    val featuredImageUrl: String?,
    val featuredMedia: MediaSummaryResponse?,
    val author: UserSummaryResponse,
    val tags: List<TagSummaryResponse>,
    val status: PostStatus,
    val metaTitle: String?,
    val metaDescription: String?,
    val metaKeywords: String?,
    val publishedAt: LocalDateTime?,
    val scheduledAt: LocalDateTime?,
    val viewCount: Long,
    val readingTimeMinutes: Int?,
    val allowComments: Boolean,
    val featured: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(blogPost: BlogPost): BlogPostResponse {
            return BlogPostResponse(
                id = blogPost.id!!,
                title = blogPost.title,
                slug = blogPost.slug,
                excerpt = blogPost.excerpt,
                mdxContent = blogPost.mdxContent,
                featuredImageUrl = blogPost.featuredImageUrl,
                featuredMedia = blogPost.featuredMedia?.let { MediaSummaryResponse.fromEntity(it) },
                author = UserSummaryResponse.fromEntity(blogPost.author),
                tags = blogPost.tags.map { TagSummaryResponse.fromEntity(it) },
                status = blogPost.status,
                metaTitle = blogPost.metaTitle,
                metaDescription = blogPost.metaDescription,
                metaKeywords = blogPost.metaKeywords,
                publishedAt = blogPost.publishedAt,
                scheduledAt = blogPost.scheduledAt,
                viewCount = blogPost.viewCount,
                readingTimeMinutes = blogPost.readingTimeMinutes,
                allowComments = blogPost.allowComments,
                featured = blogPost.featured,
                createdAt = blogPost.createdAt,
                updatedAt = blogPost.updatedAt
            )
        }
    }
}

data class BlogPostSummaryResponse(
    val id: Long,
    val title: String,
    val slug: String,
    val excerpt: String?,
    val featuredImageUrl: String?,
    val author: UserSummaryResponse,
    val tags: List<TagSummaryResponse>,
    val status: PostStatus,
    val publishedAt: LocalDateTime?,
    val viewCount: Long,
    val readingTimeMinutes: Int?,
    val featured: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun fromEntity(blogPost: BlogPost): BlogPostSummaryResponse {
            return BlogPostSummaryResponse(
                id = blogPost.id!!,
                title = blogPost.title,
                slug = blogPost.slug,
                excerpt = blogPost.excerpt,
                featuredImageUrl = blogPost.featuredImageUrl,
                author = UserSummaryResponse.fromEntity(blogPost.author),
                tags = blogPost.tags.map { TagSummaryResponse.fromEntity(it) },
                status = blogPost.status,
                publishedAt = blogPost.publishedAt,
                viewCount = blogPost.viewCount,
                readingTimeMinutes = blogPost.readingTimeMinutes,
                featured = blogPost.featured,
                createdAt = blogPost.createdAt
            )
        }
    }
}
