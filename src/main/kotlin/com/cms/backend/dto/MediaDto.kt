package com.cms.backend.dto

import com.cms.backend.entity.Media
import com.cms.backend.entity.MediaType
import java.time.LocalDateTime

// Request DTOs
data class UpdateMediaRequest(
    val altText: String? = null,
    val caption: String? = null
)

// Response DTOs
data class MediaResponse(
    val id: Long,
    val fileName: String,
    val originalFileName: String,
    val fileUrl: String,
    val contentType: String,
    val fileSize: Long,
    val mediaType: MediaType,
    val width: Int?,
    val height: Int?,
    val altText: String?,
    val caption: String?,
    val uploadedBy: UserSummaryResponse?,
    val uploadedAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(media: Media): MediaResponse {
            return MediaResponse(
                id = media.id!!,
                fileName = media.fileName,
                originalFileName = media.originalFileName,
                fileUrl = media.fileUrl,
                contentType = media.contentType,
                fileSize = media.fileSize,
                mediaType = media.mediaType,
                width = media.width,
                height = media.height,
                altText = media.altText,
                caption = media.caption,
                uploadedBy = media.uploadedBy?.let { UserSummaryResponse.fromEntity(it) },
                uploadedAt = media.uploadedAt,
                updatedAt = media.updatedAt
            )
        }
    }
}

data class MediaSummaryResponse(
    val id: Long,
    val fileName: String,
    val fileUrl: String,
    val contentType: String,
    val mediaType: MediaType
) {
    companion object {
        fun fromEntity(media: Media): MediaSummaryResponse {
            return MediaSummaryResponse(
                id = media.id!!,
                fileName = media.fileName,
                fileUrl = media.fileUrl,
                contentType = media.contentType,
                mediaType = media.mediaType
            )
        }
    }
}





