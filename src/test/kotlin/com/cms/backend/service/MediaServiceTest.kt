package com.cms.backend.service

import com.cms.backend.dto.UpdateMediaRequest
import com.cms.backend.entity.Media
import com.cms.backend.entity.MediaType
import com.cms.backend.entity.User
import com.cms.backend.entity.UserRole
import com.cms.backend.exception.ResourceNotFoundException
import com.cms.backend.repository.MediaRepository
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
import org.springframework.mock.web.MockMultipartFile
import java.util.*

class MediaServiceTest {

    private val mediaRepository: MediaRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val s3Service: S3Service = mockk()
    private val mediaService = MediaService(mediaRepository, userRepository, s3Service)

    @Test
    fun `should upload media successfully`() {
        // Given
        val file = MockMultipartFile("file", "test.jpg", "image/jpeg", "content".toByteArray())
        val uploader = User(
            id = 1L,
            email = "user@example.com",
            password = "password",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            role = UserRole.AUTHOR
        )

        every { userRepository.findById(1L) } returns Optional.of(uploader)
        every { s3Service.uploadFile(file) } returns Pair("s3-key.jpg", "https://bucket.s3.amazonaws.com/s3-key.jpg")

        val mediaSlot = slot<Media>()
        every { mediaRepository.save(capture(mediaSlot)) } answers { mediaSlot.captured.copy(id = 1L) }

        // When
        val result = mediaService.uploadMedia(file, 1L, "alt text", "caption")

        // Then
        assertThat(result.fileName).isEqualTo("s3-key.jpg")
        assertThat(result.fileUrl).isEqualTo("https://bucket.s3.amazonaws.com/s3-key.jpg")
        assertThat(result.mediaType).isEqualTo(MediaType.IMAGE)
        verify(exactly = 1) { s3Service.uploadFile(file) }
        verify(exactly = 1) { mediaRepository.save(any()) }
    }

    @Test
    fun `should get media by id`() {
        // Given
        val media = createTestMedia(1L, "test.jpg", MediaType.IMAGE)

        every { mediaRepository.findById(1L) } returns Optional.of(media)

        // When
        val result = mediaService.getMediaById(1L)

        // Then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.fileName).isEqualTo("test.jpg")
    }

    @Test
    fun `should throw exception when media not found`() {
        // Given
        every { mediaRepository.findById(999L) } returns Optional.empty()

        // When & Then
        assertThatThrownBy { mediaService.getMediaById(999L) }
            .isInstanceOf(ResourceNotFoundException::class.java)
            .hasMessageContaining("Media with id 999 not found")
    }

    @Test
    fun `should get all media paginated`() {
        // Given
        val mediaList = listOf(
            createTestMedia(1L, "image1.jpg", MediaType.IMAGE),
            createTestMedia(2L, "video1.mp4", MediaType.VIDEO)
        )

        val pageable = PageRequest.of(0, 10)
        every { mediaRepository.findAll(pageable) } returns PageImpl(mediaList)

        // When
        val result = mediaService.getAllMedia(pageable)

        // Then
        assertThat(result.content).hasSize(2)
        assertThat(result.totalElements).isEqualTo(2)
    }

    @Test
    fun `should get media by type`() {
        // Given
        val images = listOf(createTestMedia(1L, "image1.jpg", MediaType.IMAGE))

        val pageable = PageRequest.of(0, 10)
        every { mediaRepository.findAllByMediaType(MediaType.IMAGE, pageable) } returns PageImpl(images)

        // When
        val result = mediaService.getMediaByType(MediaType.IMAGE, pageable)

        // Then
        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].mediaType).isEqualTo(MediaType.IMAGE)
    }

    @Test
    fun `should get media by uploader`() {
        // Given
        val mediaList = listOf(createTestMedia(1L, "test.jpg", MediaType.IMAGE))

        val pageable = PageRequest.of(0, 10)
        every { mediaRepository.findAllByUploadedById(1L, pageable) } returns PageImpl(mediaList)

        // When
        val result = mediaService.getMediaByUploader(1L, pageable)

        // Then
        assertThat(result.content).hasSize(1)
    }

    @Test
    fun `should delete media successfully`() {
        // Given
        val media = createTestMedia(1L, "test.jpg", MediaType.IMAGE)

        every { mediaRepository.findById(1L) } returns Optional.of(media)
        every { s3Service.deleteFile("s3-key-123") } returns Unit
        every { mediaRepository.delete(media) } returns Unit

        // When
        mediaService.deleteMedia(1L)

        // Then
        verify(exactly = 1) { s3Service.deleteFile("s3-key-123") }
        verify(exactly = 1) { mediaRepository.delete(media) }
    }

    @Test
    fun `should determine media type from content type - image`() {
        // Given
        val file = MockMultipartFile("file", "test.png", "image/png", "content".toByteArray())
        val uploader = createTestUser()

        every { userRepository.findById(1L) } returns Optional.of(uploader)
        every { s3Service.uploadFile(file) } returns Pair("s3-key.png", "https://bucket.s3.amazonaws.com/s3-key.png")

        val mediaSlot = slot<Media>()
        every { mediaRepository.save(capture(mediaSlot)) } answers { mediaSlot.captured.copy(id = 1L) }

        // When
        mediaService.uploadMedia(file, 1L, null, null)

        // Then
        assertThat(mediaSlot.captured.mediaType).isEqualTo(MediaType.IMAGE)
    }

    @Test
    fun `should determine media type from content type - video`() {
        // Given
        val file = MockMultipartFile("file", "test.mp4", "video/mp4", "content".toByteArray())
        val uploader = createTestUser()

        every { userRepository.findById(1L) } returns Optional.of(uploader)
        every { s3Service.uploadFile(file) } returns Pair("s3-key.mp4", "https://bucket.s3.amazonaws.com/s3-key.mp4")

        val mediaSlot = slot<Media>()
        every { mediaRepository.save(capture(mediaSlot)) } answers { mediaSlot.captured.copy(id = 1L) }

        // When
        mediaService.uploadMedia(file, 1L, null, null)

        // Then
        assertThat(mediaSlot.captured.mediaType).isEqualTo(MediaType.VIDEO)
    }

    @Test
    fun `should default to DOCUMENT for PDF content type`() {
        // Given
        val file = MockMultipartFile("file", "test.pdf", "application/pdf", "content".toByteArray())
        val uploader = createTestUser()

        every { userRepository.findById(1L) } returns Optional.of(uploader)
        every { s3Service.uploadFile(file) } returns Pair("s3-key.pdf", "https://bucket.s3.amazonaws.com/s3-key.pdf")

        val mediaSlot = slot<Media>()
        every { mediaRepository.save(capture(mediaSlot)) } answers { mediaSlot.captured.copy(id = 1L) }

        // When
        mediaService.uploadMedia(file, 1L, null, null)

        // Then
        assertThat(mediaSlot.captured.mediaType).isEqualTo(MediaType.DOCUMENT)
    }

    @Test
    fun `should update media successfully`() {
        // Given
        val existingMedia = createTestMedia(1L, "old.jpg", MediaType.IMAGE)
        val updateRequest = UpdateMediaRequest(
            altText = "New alt text",
            caption = "New caption"
        )

        every { mediaRepository.findById(1L) } returns Optional.of(existingMedia)

        val updatedMediaSlot = slot<Media>()
        every { mediaRepository.save(capture(updatedMediaSlot)) } answers { updatedMediaSlot.captured }

        // When
        val result = mediaService.updateMedia(1L, updateRequest)

        // Then
        assertThat(result.altText).isEqualTo("New alt text")
        assertThat(result.caption).isEqualTo("New caption")
    }

    @Test
    fun `should upload media without uploader`() {
        // Given
        val file = MockMultipartFile("file", "test.jpg", "image/jpeg", "content".toByteArray())

        every { s3Service.uploadFile(file) } returns Pair("s3-key.jpg", "https://bucket.s3.amazonaws.com/s3-key.jpg")

        val mediaSlot = slot<Media>()
        every { mediaRepository.save(capture(mediaSlot)) } answers { mediaSlot.captured.copy(id = 1L) }

        // When
        val result = mediaService.uploadMedia(file, null, null, null)

        // Then
        assertThat(result.fileName).isEqualTo("s3-key.jpg")
        assertThat(result.uploadedBy).isNull()
        assertThat(mediaSlot.captured.uploadedBy).isNull()
    }

    private fun createTestMedia(id: Long, fileName: String, mediaType: MediaType): Media {
        return Media(
            id = id,
            fileName = fileName,
            originalFileName = fileName,
            fileUrl = "https://bucket.s3.amazonaws.com/$fileName",
            s3Key = "s3-key-123",
            contentType = when (mediaType) {
                MediaType.IMAGE -> "image/jpeg"
                MediaType.VIDEO -> "video/mp4"
                MediaType.AUDIO -> "audio/mp3"
                MediaType.DOCUMENT -> "application/pdf"
                MediaType.OTHER -> "application/octet-stream"
            },
            fileSize = 1024,
            mediaType = mediaType,
            uploadedBy = createTestUser()
        )
    }

    private fun createTestUser(): User {
        return User(
            id = 1L,
            email = "user@example.com",
            password = "password",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            role = UserRole.AUTHOR
        )
    }
}

