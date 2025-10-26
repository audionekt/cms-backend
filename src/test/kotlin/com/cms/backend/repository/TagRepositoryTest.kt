package com.cms.backend.repository

import com.cms.backend.entity.Tag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class TagRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var tagRepository: TagRepository

    @Test
    fun `should find tag by slug`() {
        // Given
        val tag = Tag(name = "Kotlin", slug = "kotlin")
        entityManager.persist(tag)
        entityManager.flush()

        // When
        val found = tagRepository.findBySlug("kotlin")

        // Then
        assertThat(found).isPresent
        assertThat(found.get().name).isEqualTo("Kotlin")
    }

    @Test
    fun `should return empty when slug not found`() {
        // When
        val found = tagRepository.findBySlug("nonexistent")

        // Then
        assertThat(found).isEmpty
    }

    @Test
    fun `should check if slug exists`() {
        // Given
        val tag = Tag(name = "Spring Boot", slug = "spring-boot")
        entityManager.persist(tag)
        entityManager.flush()

        // When & Then
        assertThat(tagRepository.existsBySlug("spring-boot")).isTrue
        assertThat(tagRepository.existsBySlug("nonexistent")).isFalse
    }

    @Test
    fun `should enforce unique slug constraint`() {
        // Given
        val tag1 = Tag(name = "First", slug = "unique-slug")
        entityManager.persist(tag1)
        entityManager.flush()

        // When & Then
        val tag2 = Tag(name = "Second", slug = "unique-slug")
        
        try {
            entityManager.persist(tag2)
            entityManager.flush()
            assertThat(false).isTrue()  // Should not reach here
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(Exception::class.java)
        }
    }

    @Test
    fun `should save and retrieve tag`() {
        // Given
        val tag = Tag(name = "TypeScript", slug = "typescript")

        // When
        val saved = tagRepository.save(tag)
        val found = tagRepository.findById(saved.id!!).get()

        // Then
        assertThat(found.name).isEqualTo("TypeScript")
        assertThat(found.slug).isEqualTo("typescript")
        assertThat(found.createdAt).isNotNull
        assertThat(found.updatedAt).isNotNull
    }

    @Test
    fun `should find all tags`() {
        // Given
        val tag1 = Tag(name = "React", slug = "react")
        val tag2 = Tag(name = "Vue", slug = "vue")
        val tag3 = Tag(name = "Angular", slug = "angular")
        entityManager.persist(tag1)
        entityManager.persist(tag2)
        entityManager.persist(tag3)
        entityManager.flush()

        // When
        val allTags = tagRepository.findAll()

        // Then
        assertThat(allTags).hasSize(3)
        assertThat(allTags.map { it.name }).containsExactlyInAnyOrder("React", "Vue", "Angular")
    }
}

