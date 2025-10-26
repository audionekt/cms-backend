package com.cms.backend.service

import com.cms.backend.dto.CreateTagRequest
import com.cms.backend.dto.UpdateTagRequest
import com.cms.backend.entity.Tag
import com.cms.backend.exception.DuplicateResourceException
import com.cms.backend.exception.ResourceNotFoundException
import com.cms.backend.repository.TagRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.*

class TagServiceTest {

    private val tagRepository: TagRepository = mockk()
    private val tagService = TagService(tagRepository)

    @Test
    fun `should create tag successfully`() {
        // Given
        val request = CreateTagRequest(name = "Kotlin", slug = "kotlin")

        every { tagRepository.existsBySlug("kotlin") } returns false
        every { tagRepository.existsByName("Kotlin") } returns false

        val tagSlot = slot<Tag>()
        every { tagRepository.save(capture(tagSlot)) } answers { tagSlot.captured.copy(id = 1L) }

        // When
        val result = tagService.createTag(request)

        // Then
        assertThat(result.name).isEqualTo("Kotlin")
        assertThat(result.slug).isEqualTo("kotlin")
        verify(exactly = 1) { tagRepository.save(any()) }
    }

    @Test
    fun `should throw exception when slug already exists`() {
        // Given
        val request = CreateTagRequest(name = "Kotlin", slug = "existing-slug")

        every { tagRepository.existsBySlug("existing-slug") } returns true

        // When & Then
        assertThatThrownBy { tagService.createTag(request) }
            .isInstanceOf(DuplicateResourceException::class.java)
            .hasMessageContaining("Tag with slug existing-slug already exists")

        verify(exactly = 0) { tagRepository.save(any()) }
    }

    @Test
    fun `should get tag by id`() {
        // Given
        val tag = Tag(id = 1L, name = "Spring Boot", slug = "spring-boot")

        every { tagRepository.findById(1L) } returns Optional.of(tag)

        // When
        val result = tagService.getTagById(1L)

        // Then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.name).isEqualTo("Spring Boot")
        assertThat(result.slug).isEqualTo("spring-boot")
    }

    @Test
    fun `should throw exception when tag not found`() {
        // Given
        every { tagRepository.findById(999L) } returns Optional.empty()

        // When & Then
        assertThatThrownBy { tagService.getTagById(999L) }
            .isInstanceOf(ResourceNotFoundException::class.java)
            .hasMessageContaining("Tag with id 999 not found")
    }

    @Test
    fun `should get tag by slug`() {
        // Given
        val tag = Tag(id = 1L, name = "React", slug = "react")

        every { tagRepository.findBySlug("react") } returns Optional.of(tag)

        // When
        val result = tagService.getTagBySlug("react")

        // Then
        assertThat(result.slug).isEqualTo("react")
        assertThat(result.name).isEqualTo("React")
    }

    @Test
    fun `should get all tags`() {
        // Given
        val tags = listOf(
            Tag(id = 1L, name = "Kotlin", slug = "kotlin"),
            Tag(id = 2L, name = "Java", slug = "java"),
            Tag(id = 3L, name = "TypeScript", slug = "typescript")
        )

        every { tagRepository.findAll() } returns tags

        // When
        val result = tagService.getAllTags()

        // Then
        assertThat(result).hasSize(3)
        assertThat(result.map { it.name }).containsExactlyInAnyOrder("Kotlin", "Java", "TypeScript")
    }

    @Test
    fun `should update tag successfully`() {
        // Given
        val existingTag = Tag(id = 1L, name = "Old Name", slug = "old-slug")
        val updateRequest = UpdateTagRequest(name = "New Name", slug = "new-slug")

        every { tagRepository.findById(1L) } returns Optional.of(existingTag)
        every { tagRepository.existsBySlug("new-slug") } returns false
        every { tagRepository.existsByName("New Name") } returns false

        val updatedTagSlot = slot<Tag>()
        every { tagRepository.save(capture(updatedTagSlot)) } answers { updatedTagSlot.captured }

        // When
        val result = tagService.updateTag(1L, updateRequest)

        // Then
        assertThat(result.name).isEqualTo("New Name")
        assertThat(result.slug).isEqualTo("new-slug")
        assertThat(updatedTagSlot.captured.name).isEqualTo("New Name")
        assertThat(updatedTagSlot.captured.slug).isEqualTo("new-slug")
    }

    @Test
    fun `should throw exception when updating to existing slug`() {
        // Given
        val existingTag = Tag(id = 1L, name = "Tag", slug = "old-slug")
        val updateRequest = UpdateTagRequest(slug = "existing-slug")

        every { tagRepository.findById(1L) } returns Optional.of(existingTag)
        every { tagRepository.existsBySlug("existing-slug") } returns true

        // When & Then
        assertThatThrownBy { tagService.updateTag(1L, updateRequest) }
            .isInstanceOf(DuplicateResourceException::class.java)

        verify(exactly = 0) { tagRepository.save(any()) }
    }

    @Test
    fun `should delete tag successfully`() {
        // Given
        val tag = Tag(id = 1L, name = "Kotlin", slug = "kotlin")

        every { tagRepository.findById(1L) } returns Optional.of(tag)
        every { tagRepository.delete(tag) } returns Unit

        // When
        tagService.deleteTag(1L)

        // Then
        verify(exactly = 1) { tagRepository.delete(tag) }
    }

    @Test
    fun `findTagById should return tag for internal use`() {
        // Given
        val tag = Tag(id = 1L, name = "Test", slug = "test")

        every { tagRepository.findById(1L) } returns Optional.of(tag)

        // When
        val result = tagService.findTagById(1L)

        // Then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.name).isEqualTo("Test")
    }
}

