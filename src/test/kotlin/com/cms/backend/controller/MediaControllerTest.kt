package com.cms.backend.controller

import com.cms.backend.dto.MediaResponse
import com.cms.backend.entity.MediaType
import com.cms.backend.service.MediaService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(MediaController::class)
@ActiveProfiles("test")
@WithMockUser
class MediaControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var mediaService: MediaService

    @Test
    fun `should upload media successfully`() {
        // Given
        val file = MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".toByteArray())

        val response = MediaResponse(
            id = 1L,
            fileName = "test.jpg",
            originalFileName = "test.jpg",
            fileUrl = "https://bucket.s3.amazonaws.com/test.jpg",
            contentType = "image/jpeg",
            fileSize = 1024L,
            mediaType = MediaType.IMAGE,
            width = null,
            height = null,
            altText = null,
            caption = null,
            uploadedBy = null,
            uploadedAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { mediaService.uploadMedia(any(), any(), any(), any()) } returns response

        // When & Then
        mockMvc.perform(
            multipart("/api/v1/media/upload")
                .file(file)
                .with(csrf())
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.fileName").value("test.jpg"))

        verify(exactly = 1) { mediaService.uploadMedia(any(), any(), any(), any()) }
    }

    @Test
    fun `should get media by id`() {
        // Given
        val response = MediaResponse(
            id = 1L,
            fileName = "test.jpg",
            originalFileName = "test.jpg",
            fileUrl = "https://bucket.s3.amazonaws.com/test.jpg",
            contentType = "image/jpeg",
            fileSize = 1024L,
            mediaType = MediaType.IMAGE,
            width = null,
            height = null,
            altText = null,
            caption = null,
            uploadedBy = null,
            uploadedAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { mediaService.getMediaById(1L) } returns response

        // When & Then
        mockMvc.perform(get("/api/v1/media/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
    }

    @Test
    fun `should delete media`() {
        // Given
        every { mediaService.deleteMedia(1L) } returns Unit

        // When & Then
        mockMvc.perform(delete("/api/v1/media/1").with(csrf()))
            .andExpect(status().isNoContent)

        verify(exactly = 1) { mediaService.deleteMedia(1L) }
    }

    @Test
    fun `should get all media with pagination`() {
        // Given
        val mediaList = listOf(
            MediaResponse(
                id = 1L,
                fileName = "test1.jpg",
                originalFileName = "test1.jpg",
                fileUrl = "https://bucket.s3.amazonaws.com/test1.jpg",
                contentType = "image/jpeg",
                fileSize = 1024L,
                mediaType = MediaType.IMAGE,
                width = null,
                height = null,
                altText = null,
                caption = null,
                uploadedBy = null,
                uploadedAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        val pageResponse = com.cms.backend.dto.PageResponse(
            content = mediaList,
            page = 0,
            size = 20,
            totalElements = 1,
            totalPages = 1,
            last = true
        )

        every { mediaService.getAllMedia(any()) } returns pageResponse

        // When & Then
        mockMvc.perform(get("/api/v1/media"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].fileName").value("test1.jpg"))
    }

    @Test
    fun `should get media by type`() {
        // Given
        val pageResponse = com.cms.backend.dto.PageResponse(
            content = emptyList<MediaResponse>(),
            page = 0,
            size = 20,
            totalElements = 0,
            totalPages = 0,
            last = true
        )

        every { mediaService.getMediaByType(MediaType.IMAGE, any()) } returns pageResponse

        // When & Then
        mockMvc.perform(get("/api/v1/media/type/IMAGE"))
            .andExpect(status().isOk)
    }

    @Test
    fun `should update media`() {
        // Given
        val response = MediaResponse(
            id = 1L,
            fileName = "test.jpg",
            originalFileName = "test.jpg",
            fileUrl = "https://bucket.s3.amazonaws.com/test.jpg",
            contentType = "image/jpeg",
            fileSize = 1024L,
            mediaType = MediaType.IMAGE,
            width = null,
            height = null,
            altText = "Updated alt",
            caption = "Updated caption",
            uploadedBy = null,
            uploadedAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { mediaService.updateMedia(1L, any()) } returns response

        // When & Then
        mockMvc.perform(
            put("/api/v1/media/1")
                .with(csrf())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("""{"altText":"Updated alt","caption":"Updated caption"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.altText").value("Updated alt"))
    }
}

