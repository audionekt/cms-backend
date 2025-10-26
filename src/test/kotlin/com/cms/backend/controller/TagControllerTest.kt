package com.cms.backend.controller

import com.cms.backend.dto.CreateTagRequest
import com.cms.backend.dto.PageResponse
import com.cms.backend.dto.TagResponse
import com.cms.backend.service.TagService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(TagController::class)
@ActiveProfiles("test")
@WithMockUser
class TagControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var tagService: TagService

    @Test
    fun `should create tag successfully`() {
        // Given
        val request = CreateTagRequest(name = "Kotlin", slug = "kotlin")

        val response = TagResponse(
            id = 1L,
            name = "Kotlin",
            slug = "kotlin",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { tagService.createTag(any()) } returns response

        // When & Then
        mockMvc.perform(
            post("/api/v1/tags")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Kotlin"))
            .andExpect(jsonPath("$.slug").value("kotlin"))

        verify(exactly = 1) { tagService.createTag(any()) }
    }

    @Test
    fun `should get tag by id`() {
        // Given
        val response = TagResponse(
            id = 1L,
            name = "Kotlin",
            slug = "kotlin",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { tagService.getTagById(1L) } returns response

        // When & Then
        mockMvc.perform(get("/api/v1/tags/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Kotlin"))
    }

    @Test
    fun `should get all tags`() {
        // Given
        val tags = listOf(
            TagResponse(
                id = 1L,
                name = "Kotlin",
                slug = "kotlin",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            TagResponse(
                id = 2L,
                name = "Java",
                slug = "java",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        every { tagService.getAllTags() } returns tags

        // When & Then
        mockMvc.perform(get("/api/v1/tags"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].name").value("Kotlin"))
            .andExpect(jsonPath("$[1].name").value("Java"))
    }

    @Test
    fun `should delete tag`() {
        // Given
        every { tagService.deleteTag(1L) } returns Unit

        // When & Then
        mockMvc.perform(delete("/api/v1/tags/1").with(csrf()))
            .andExpect(status().isNoContent)

        verify(exactly = 1) { tagService.deleteTag(1L) }
    }
}

