package com.cms.backend.controller

import com.cms.backend.dto.CreateUserRequest
import com.cms.backend.dto.UserResponse
import com.cms.backend.entity.UserRole
import com.cms.backend.service.UserService
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

@WebMvcTest(UserController::class)
@ActiveProfiles("test")
@WithMockUser
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var userService: UserService

    @Test
    fun `should create user successfully`() {
        // Given
        val request = CreateUserRequest(
            email = "test@example.com",
            password = "password123",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe"
        )

        val response = UserResponse(
            id = 1L,
            email = "test@example.com",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            bio = null,
            avatarUrl = null,
            role = UserRole.AUTHOR,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { userService.createUser(any()) } returns response

        // When & Then
        mockMvc.perform(
            post("/api/v1/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.username").value("johndoe"))

        verify(exactly = 1) { userService.createUser(any()) }
    }

    @Test
    fun `should get user by id`() {
        // Given
        val response = UserResponse(
            id = 1L,
            email = "test@example.com",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            bio = null,
            avatarUrl = null,
            role = UserRole.AUTHOR,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { userService.getUserById(1L) } returns response

        // When & Then
        mockMvc.perform(get("/api/v1/users/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("johndoe"))
    }

    @Test
    fun `should get all users`() {
        // Given
        val users = listOf(
            UserResponse(
                id = 1L,
                email = "user1@example.com",
                firstName = "User",
                lastName = "One",
                username = "user1",
                bio = null,
                avatarUrl = null,
                role = UserRole.AUTHOR,
                active = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        every { userService.getAllUsers() } returns users

        // When & Then
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].username").value("user1"))
    }

    @Test
    fun `should delete user`() {
        // Given
        every { userService.deleteUser(1L) } returns Unit

        // When & Then
        mockMvc.perform(delete("/api/v1/users/1").with(csrf()))
            .andExpect(status().isNoContent)

        verify(exactly = 1) { userService.deleteUser(1L) }
    }
}

