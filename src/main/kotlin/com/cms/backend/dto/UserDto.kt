package com.cms.backend.dto

import com.cms.backend.entity.User
import com.cms.backend.entity.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

// Request DTOs
data class CreateUserRequest(
    @field:Email(message = "Email must be valid")
    @field:NotBlank(message = "Email is required")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String,

    @field:NotBlank(message = "First name is required")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    val lastName: String,

    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    val username: String,

    val bio: String? = null,
    val avatarUrl: String? = null,
    val role: UserRole = UserRole.AUTHOR
)

data class UpdateUserRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val bio: String? = null,
    val avatarUrl: String? = null,
    val role: UserRole? = null,
    val active: Boolean? = null
)

// Response DTOs
data class UserResponse(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val bio: String?,
    val avatarUrl: String?,
    val role: UserRole,
    val active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(user: User): UserResponse {
            return UserResponse(
                id = user.id!!,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                username = user.username,
                bio = user.bio,
                avatarUrl = user.avatarUrl,
                role = user.role,
                active = user.active,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
        }
    }
}

data class UserSummaryResponse(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String?
) {
    companion object {
        fun fromEntity(user: User): UserSummaryResponse {
            return UserSummaryResponse(
                id = user.id!!,
                username = user.username,
                firstName = user.firstName,
                lastName = user.lastName,
                avatarUrl = user.avatarUrl
            )
        }
    }
}
