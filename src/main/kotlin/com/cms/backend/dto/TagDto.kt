package com.cms.backend.dto

import com.cms.backend.entity.Tag
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

// Request DTOs
data class CreateTagRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 50, message = "Tag name must be between 2 and 50 characters")
    val name: String,

    @field:NotBlank(message = "Slug is required")
    @field:Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be lowercase alphanumeric with hyphens")
    @field:Size(min = 2, max = 50, message = "Slug must be between 2 and 50 characters")
    val slug: String
)

data class UpdateTagRequest(
    val name: String? = null,
    val slug: String? = null
)

// Response DTOs
data class TagResponse(
    val id: Long,
    val name: String,
    val slug: String,
    val postCount: Int = 0,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(tag: Tag): TagResponse {
            return TagResponse(
                id = tag.id!!,
                name = tag.name,
                slug = tag.slug,
                postCount = tag.blogPosts.size,
                createdAt = tag.createdAt,
                updatedAt = tag.updatedAt
            )
        }
    }
}

data class TagSummaryResponse(
    val id: Long,
    val name: String,
    val slug: String
) {
    companion object {
        fun fromEntity(tag: Tag): TagSummaryResponse {
            return TagSummaryResponse(
                id = tag.id!!,
                name = tag.name,
                slug = tag.slug
            )
        }
    }
}
