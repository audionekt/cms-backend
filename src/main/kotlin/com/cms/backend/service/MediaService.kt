package com.cms.backend.service

import com.cms.backend.dto.MediaResponse
import com.cms.backend.dto.PageResponse
import com.cms.backend.dto.UpdateMediaRequest
import com.cms.backend.entity.Media
import com.cms.backend.entity.MediaType
import com.cms.backend.exception.ResourceNotFoundException
import com.cms.backend.repository.MediaRepository
import com.cms.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class MediaService(
    private val mediaRepository: MediaRepository,
    private val userRepository: UserRepository,
    private val s3Service: S3Service
) {
    private val logger = LoggerFactory.getLogger(MediaService::class.java)

    fun uploadMedia(
        file: MultipartFile,
        uploadedById: Long?,
        altText: String?,
        caption: String?
    ): MediaResponse {
        logger.info("Uploading media file: ${file.originalFilename}")

        val (s3Key, fileUrl) = s3Service.uploadFile(file)

        val uploadedBy = uploadedById?.let {
            userRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("User with id $it not found") }
        }

        val media = Media(
            fileName = s3Key,
            originalFileName = file.originalFilename ?: "unknown",
            fileUrl = fileUrl,
            s3Key = s3Key,
            contentType = file.contentType ?: "application/octet-stream",
            fileSize = file.size,
            mediaType = determineMediaType(file.contentType),
            altText = altText,
            caption = caption,
            uploadedBy = uploadedBy
        )

        val savedMedia = mediaRepository.save(media)
        logger.info("Media uploaded successfully with id: ${savedMedia.id}")
        return MediaResponse.fromEntity(savedMedia)
    }

    @Transactional(readOnly = true)
    fun getMediaById(id: Long): MediaResponse {
        val media = findMediaById(id)
        return MediaResponse.fromEntity(media)
    }

    @Transactional(readOnly = true)
    fun getAllMedia(pageable: Pageable): PageResponse<MediaResponse> {
        val page = mediaRepository.findAll(pageable)
        return PageResponse.fromPage(page) { MediaResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getMediaByType(mediaType: MediaType, pageable: Pageable): PageResponse<MediaResponse> {
        val page = mediaRepository.findAllByMediaType(mediaType, pageable)
        return PageResponse.fromPage(page) { MediaResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getMediaByUploader(uploadedById: Long, pageable: Pageable): PageResponse<MediaResponse> {
        val page = mediaRepository.findAllByUploadedById(uploadedById, pageable)
        return PageResponse.fromPage(page) { MediaResponse.fromEntity(it) }
    }

    fun updateMedia(id: Long, request: UpdateMediaRequest): MediaResponse {
        logger.info("Updating media with id: $id")
        val media = findMediaById(id)

        val updatedMedia = media.copy(
            altText = request.altText ?: media.altText,
            caption = request.caption ?: media.caption
        )

        val savedMedia = mediaRepository.save(updatedMedia)
        logger.info("Media updated successfully with id: ${savedMedia.id}")
        return MediaResponse.fromEntity(savedMedia)
    }

    fun deleteMedia(id: Long) {
        logger.info("Deleting media with id: $id")
        val media = findMediaById(id)

        // Delete from S3
        s3Service.deleteFile(media.s3Key)

        // Delete from database
        mediaRepository.delete(media)
        logger.info("Media deleted successfully with id: $id")
    }

    fun findMediaById(id: Long): Media {
        return mediaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Media with id $id not found") }
    }

    private fun determineMediaType(contentType: String?): MediaType {
        return when {
            contentType?.startsWith("image/") == true -> MediaType.IMAGE
            contentType?.startsWith("video/") == true -> MediaType.VIDEO
            contentType?.startsWith("audio/") == true -> MediaType.AUDIO
            contentType?.contains("pdf") == true || 
            contentType?.contains("document") == true ||
            contentType?.contains("text") == true -> MediaType.DOCUMENT
            else -> MediaType.OTHER
        }
    }
}
