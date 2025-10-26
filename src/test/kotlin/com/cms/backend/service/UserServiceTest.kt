package com.cms.backend.service

import com.cms.backend.dto.CreateUserRequest
import com.cms.backend.dto.UpdateUserRequest
import com.cms.backend.entity.User
import com.cms.backend.entity.UserRole
import com.cms.backend.exception.DuplicateResourceException
import com.cms.backend.exception.ResourceNotFoundException
import com.cms.backend.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class UserServiceTest {

    private val userRepository: UserRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val userService = UserService(userRepository, passwordEncoder)

    @Test
    fun `should create user with encoded password`() {
        // Given
        val request = CreateUserRequest(
            email = "test@example.com",
            password = "plainPassword",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            role = UserRole.AUTHOR
        )

        every { userRepository.existsByEmail("test@example.com") } returns false
        every { userRepository.existsByUsername("johndoe") } returns false
        every { passwordEncoder.encode("plainPassword") } returns "encodedPassword"

        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured.copy(id = 1L) }

        // When
        val result = userService.createUser(request)

        // Then
        assertThat(result.email).isEqualTo("test@example.com")
        assertThat(result.username).isEqualTo("johndoe")
        assertThat(result.firstName).isEqualTo("John")
        assertThat(result.lastName).isEqualTo("Doe")

        verify(exactly = 1) { passwordEncoder.encode("plainPassword") }
        verify(exactly = 1) { userRepository.save(any()) }
        
        // Verify password was encoded
        assertThat(userSlot.captured.password).isEqualTo("encodedPassword")
    }

    @Test
    fun `should throw exception when email already exists`() {
        // Given
        val request = CreateUserRequest(
            email = "existing@example.com",
            password = "password",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe"
        )

        every { userRepository.existsByEmail("existing@example.com") } returns true

        // When & Then
        assertThatThrownBy { userService.createUser(request) }
            .isInstanceOf(DuplicateResourceException::class.java)
            .hasMessage("User with email existing@example.com already exists")

        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { passwordEncoder.encode(any()) }
    }

    @Test
    fun `should throw exception when username already exists`() {
        // Given
        val request = CreateUserRequest(
            email = "new@example.com",
            password = "password",
            firstName = "John",
            lastName = "Doe",
            username = "existinguser"
        )

        every { userRepository.existsByEmail("new@example.com") } returns false
        every { userRepository.existsByUsername("existinguser") } returns true

        // When & Then
        assertThatThrownBy { userService.createUser(request) }
            .isInstanceOf(DuplicateResourceException::class.java)
            .hasMessage("User with username existinguser already exists")

        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { passwordEncoder.encode(any()) }
    }

    @Test
    fun `should get user by id`() {
        // Given
        val user = User(
            id = 1L,
            email = "user@example.com",
            password = "encoded",
            firstName = "Jane",
            lastName = "Smith",
            username = "janesmith",
            role = UserRole.EDITOR
        )

        every { userRepository.findById(1L) } returns Optional.of(user)

        // When
        val result = userService.getUserById(1L)

        // Then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.email).isEqualTo("user@example.com")
        assertThat(result.username).isEqualTo("janesmith")
    }

    @Test
    fun `should throw exception when user not found by id`() {
        // Given
        every { userRepository.findById(999L) } returns Optional.empty()

        // When & Then
        assertThatThrownBy { userService.getUserById(999L) }
            .isInstanceOf(ResourceNotFoundException::class.java)
            .hasMessageContaining("User with id 999 not found")
    }

    @Test
    fun `should get user by username`() {
        // Given
        val user = User(
            id = 1L,
            email = "user@example.com",
            password = "encoded",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            role = UserRole.AUTHOR
        )

        every { userRepository.findByUsername("johndoe") } returns Optional.of(user)

        // When
        val result = userService.getUserByUsername("johndoe")

        // Then
        assertThat(result.username).isEqualTo("johndoe")
        assertThat(result.email).isEqualTo("user@example.com")
    }

    @Test
    fun `should throw exception when user not found by username`() {
        // Given
        every { userRepository.findByUsername("nonexistent") } returns Optional.empty()

        // When & Then
        assertThatThrownBy { userService.getUserByUsername("nonexistent") }
            .isInstanceOf(ResourceNotFoundException::class.java)
            .hasMessageContaining("User with username nonexistent not found")
    }

    @Test
    fun `should update user successfully`() {
        // Given
        val existingUser = User(
            id = 1L,
            email = "old@example.com",
            password = "encoded",
            firstName = "Old",
            lastName = "Name",
            username = "oldname",
            role = UserRole.AUTHOR,
            bio = "Old bio"
        )

        val updateRequest = UpdateUserRequest(
            firstName = "New",
            lastName = "Name",
            bio = "New bio",
            role = UserRole.EDITOR
        )

        every { userRepository.findById(1L) } returns Optional.of(existingUser)
        
        val updatedUserSlot = slot<User>()
        every { userRepository.save(capture(updatedUserSlot)) } answers { updatedUserSlot.captured }

        // When
        val result = userService.updateUser(1L, updateRequest)

        // Then
        assertThat(result.firstName).isEqualTo("New")
        assertThat(result.bio).isEqualTo("New bio")
        assertThat(result.role).isEqualTo(UserRole.EDITOR)
        
        // Verify unchanged fields
        assertThat(updatedUserSlot.captured.email).isEqualTo("old@example.com")
        assertThat(updatedUserSlot.captured.username).isEqualTo("oldname")
    }

    @Test
    fun `should delete user successfully`() {
        // Given
        val user = User(
            id = 1L,
            email = "user@example.com",
            password = "encoded",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            role = UserRole.AUTHOR
        )

        every { userRepository.findById(1L) } returns Optional.of(user)
        every { userRepository.delete(user) } returns Unit

        // When
        userService.deleteUser(1L)

        // Then
        verify(exactly = 1) { userRepository.delete(user) }
    }

    @Test
    fun `should throw exception when deleting non-existent user`() {
        // Given
        every { userRepository.findById(999L) } returns Optional.empty()

        // When & Then
        assertThatThrownBy { userService.deleteUser(999L) }
            .isInstanceOf(ResourceNotFoundException::class.java)
    }
}

