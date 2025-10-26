package com.cms.backend.controller

import com.cms.backend.dto.CreateTagRequest
import com.cms.backend.dto.TagResponse
import com.cms.backend.dto.UpdateTagRequest
import com.cms.backend.service.TagService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tags")
class TagController(
    private val tagService: TagService
) {

    @PostMapping
    fun createTag(@Valid @RequestBody request: CreateTagRequest): ResponseEntity<TagResponse> {
        val tag = tagService.createTag(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(tag)
    }

    @GetMapping("/{id}")
    fun getTagById(@PathVariable id: Long): ResponseEntity<TagResponse> {
        val tag = tagService.getTagById(id)
        return ResponseEntity.ok(tag)
    }

    @GetMapping("/slug/{slug}")
    fun getTagBySlug(@PathVariable slug: String): ResponseEntity<TagResponse> {
        val tag = tagService.getTagBySlug(slug)
        return ResponseEntity.ok(tag)
    }

    @GetMapping
    fun getAllTags(): ResponseEntity<List<TagResponse>> {
        val tags = tagService.getAllTags()
        return ResponseEntity.ok(tags)
    }

    @PutMapping("/{id}")
    fun updateTag(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateTagRequest
    ): ResponseEntity<TagResponse> {
        val tag = tagService.updateTag(id, request)
        return ResponseEntity.ok(tag)
    }

    @DeleteMapping("/{id}")
    fun deleteTag(@PathVariable id: Long): ResponseEntity<Void> {
        tagService.deleteTag(id)
        return ResponseEntity.noContent().build()
    }
}





