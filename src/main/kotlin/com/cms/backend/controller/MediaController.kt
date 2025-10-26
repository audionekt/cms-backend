package com.cms.backend.controller

import com.cms.backend.dto.MediaResponse
import com.cms.backend.dto.PageResponse
import com.cms.backend.dto.UpdateMediaRequest
import com.cms.backend.entity.MediaType
import com.cms.backend.service.MediaService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/media")
class MediaController(
    private val mediaService: MediaService
) {

    @PostMapping("/upload")
    fun uploadMedia(
        @RequestParam("file") file: MultipartFile,
        @RequestParam(required = false) uploadedById: Long?,
        @RequestParam(required = false) altText: String?,
        @RequestParam(required = false) caption: String?
    ): ResponseEntity<MediaResponse> {
        val media = mediaService.uploadMedia(file, uploadedById, altText, caption)
        return ResponseEntity.status(HttpStatus.CREATED).body(media)
    }

    @GetMapping("/{id}")
    fun getMediaById(@PathVariable id: Long): ResponseEntity<MediaResponse> {
        val media = mediaService.getMediaById(id)
        return ResponseEntity.ok(media)
    }

    @GetMapping
    fun getAllMedia(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "uploadedAt") sortBy: String,
        @RequestParam(defaultValue = "DESC") sortDir: String
    ): ResponseEntity<PageResponse<MediaResponse>> {
        val direction = if (sortDir.uppercase() == "ASC") Sort.Direction.ASC else Sort.Direction.DESC
        val pageable = PageRequest.of(page, size, Sort.by(direction, sortBy))
        val media = mediaService.getAllMedia(pageable)
        return ResponseEntity.ok(media)
    }

    @GetMapping("/type/{mediaType}")
    fun getMediaByType(
        @PathVariable mediaType: MediaType,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<MediaResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedAt"))
        val media = mediaService.getMediaByType(mediaType, pageable)
        return ResponseEntity.ok(media)
    }

    @GetMapping("/user/{uploadedById}")
    fun getMediaByUploader(
        @PathVariable uploadedById: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<MediaResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedAt"))
        val media = mediaService.getMediaByUploader(uploadedById, pageable)
        return ResponseEntity.ok(media)
    }

    @PutMapping("/{id}")
    fun updateMedia(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateMediaRequest
    ): ResponseEntity<MediaResponse> {
        val media = mediaService.updateMedia(id, request)
        return ResponseEntity.ok(media)
    }

    @DeleteMapping("/{id}")
    fun deleteMedia(@PathVariable id: Long): ResponseEntity<Void> {
        mediaService.deleteMedia(id)
        return ResponseEntity.noContent().build()
    }
}

