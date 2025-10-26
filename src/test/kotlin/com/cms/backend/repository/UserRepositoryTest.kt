package com.cms.backend.repository

import com.cms.backend.entity.User
import com.cms.backend.entity.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should find user by email`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "hashedPassword",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            role = UserRole.AUTHOR
        )
        entityManager.persist(user)
        entityManager.flush()

        // When
        val found = userRepository.findByEmail("test@example.com")

        // Then
        assertThat(found).isPresent
        assertThat(found.get().username).isEqualTo("johndoe")
        assertThat(found.get().firstName).isEqualTo("John")
    }

    @Test
    fun `should return empty when email not found`() {
        // When
        val found = userRepository.findByEmail("nonexistent@example.com")

        // Then
        assertThat(found).isEmpty
    }

    @Test
    fun `should find user by username`() {
        // Given
        val user = User(
            email = "user@example.com",
            password = "hashedPassword",
            firstName = "Jane",
            lastName = "Smith",
            username = "janesmith",
            role = UserRole.EDITOR
        )
        entityManager.persist(user)
        entityManager.flush()

        // When
        val found = userRepository.findByUsername("janesmith")

        // Then
        assertThat(found).isPresent
        assertThat(found.get().email).isEqualTo("user@example.com")
        assertThat(found.get().role).isEqualTo(UserRole.EDITOR)
    }

    @Test
    fun `should return empty when username not found`() {
        // When
        val found = userRepository.findByUsername("nonexistent")

        // Then
        assertThat(found).isEmpty
    }

    @Test
    fun `should check if email exists`() {
        // Given
        val user = User(
            email = "exists@example.com",
            password = "hashedPassword",
            firstName = "Test",
            lastName = "User",
            username = "testuser",
            role = UserRole.AUTHOR
        )
        entityManager.persist(user)
        entityManager.flush()

        // When & Then
        assertThat(userRepository.existsByEmail("exists@example.com")).isTrue
        assertThat(userRepository.existsByEmail("notexists@example.com")).isFalse
    }

    @Test
    fun `should check if username exists`() {
        // Given
        val user = User(
            email = "user@example.com",
            password = "hashedPassword",
            firstName = "Test",
            lastName = "User",
            username = "existinguser",
            role = UserRole.AUTHOR
        )
        entityManager.persist(user)
        entityManager.flush()

        // When & Then
        assertThat(userRepository.existsByUsername("existinguser")).isTrue
        assertThat(userRepository.existsByUsername("notexisting")).isFalse
    }

    @Test
    fun `should enforce unique email constraint`() {
        // Given
        val user1 = User(
            email = "unique@example.com",
            password = "hashedPassword",
            firstName = "First",
            lastName = "User",
            username = "first",
            role = UserRole.AUTHOR
        )
        entityManager.persist(user1)
        entityManager.flush()

        // When & Then
        val user2 = User(
            email = "unique@example.com",  // Same email
            password = "hashedPassword",
            firstName = "Second",
            lastName = "User",
            username = "second",
            role = UserRole.AUTHOR
        )
        
        try {
            entityManager.persist(user2)
            entityManager.flush()
            assertThat(false).isTrue()  // Should not reach here
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(Exception::class.java)
        }
    }

    @Test
    fun `should save and retrieve user with all fields`() {
        // Given
        val user = User(
            email = "complete@example.com",
            password = "hashedPassword123",
            firstName = "Complete",
            lastName = "User",
            username = "completeuser",
            bio = "This is my bio",
            avatarUrl = "https://example.com/avatar.jpg",
            role = UserRole.ADMIN,
            active = true
        )

        // When
        val saved = userRepository.save(user)
        val found = userRepository.findById(saved.id!!).get()

        // Then
        assertThat(found.email).isEqualTo("complete@example.com")
        assertThat(found.bio).isEqualTo("This is my bio")
        assertThat(found.avatarUrl).isEqualTo("https://example.com/avatar.jpg")
        assertThat(found.role).isEqualTo(UserRole.ADMIN)
        assertThat(found.active).isTrue
        assertThat(found.createdAt).isNotNull
        assertThat(found.updatedAt).isNotNull
    }
}

