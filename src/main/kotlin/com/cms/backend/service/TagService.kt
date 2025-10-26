package com.cms.backend.service

import com.cms.backend.dto.CreateTagRequest
import com.cms.backend.dto.TagResponse
import com.cms.backend.dto.UpdateTagRequest
import com.cms.backend.entity.Tag
import com.cms.backend.exception.DuplicateResourceException
import com.cms.backend.exception.ResourceNotFoundException
import com.cms.backend.repository.TagRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TagService(
    private val tagRepository: TagRepository
) {
    private val logger = LoggerFactory.getLogger(TagService::class.java)

    fun createTag(request: CreateTagRequest): TagResponse {
        logger.info("Creating tag with name: ${request.name}")

        if (tagRepository.existsBySlug(request.slug)) {
            throw DuplicateResourceException("Tag with slug ${request.slug} already exists")
        }

        if (tagRepository.existsByName(request.name)) {
            throw DuplicateResourceException("Tag with name ${request.name} already exists")
        }

        val tag = Tag(
            name = request.name,
            slug = request.slug
        )

        val savedTag = tagRepository.save(tag)
        logger.info("Tag created successfully with id: ${savedTag.id}")
        return TagResponse.fromEntity(savedTag)
    }

    @Transactional(readOnly = true)
    fun getTagById(id: Long): TagResponse {
        val tag = findTagById(id)
        return TagResponse.fromEntity(tag)
    }

    @Transactional(readOnly = true)
    fun getTagBySlug(slug: String): TagResponse {
        val tag = tagRepository.findBySlug(slug)
            .orElseThrow { ResourceNotFoundException("Tag with slug $slug not found") }
        return TagResponse.fromEntity(tag)
    }

    @Transactional(readOnly = true)
    fun getAllTags(): List<TagResponse> {
        return tagRepository.findAll().map { TagResponse.fromEntity(it) }
    }

    fun updateTag(id: Long, request: UpdateTagRequest): TagResponse {
        logger.info("Updating tag with id: $id")
        val tag = findTagById(id)

        // Check for duplicate slug if it's being changed
        if (request.slug != null && request.slug != tag.slug) {
            if (tagRepository.existsBySlug(request.slug)) {
                throw DuplicateResourceException("Tag with slug ${request.slug} already exists")
            }
        }

        // Check for duplicate name if it's being changed
        if (request.name != null && request.name != tag.name) {
            if (tagRepository.existsByName(request.name)) {
                throw DuplicateResourceException("Tag with name ${request.name} already exists")
            }
        }

        val updatedTag = tag.copy(
            name = request.name ?: tag.name,
            slug = request.slug ?: tag.slug
        )

        val savedTag = tagRepository.save(updatedTag)
        logger.info("Tag updated successfully with id: ${savedTag.id}")
        return TagResponse.fromEntity(savedTag)
    }

    fun deleteTag(id: Long) {
        logger.info("Deleting tag with id: $id")
        val tag = findTagById(id)
        tagRepository.delete(tag)
        logger.info("Tag deleted successfully with id: $id")
    }

    fun findTagById(id: Long): Tag {
        return tagRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Tag with id $id not found") }
    }
}





