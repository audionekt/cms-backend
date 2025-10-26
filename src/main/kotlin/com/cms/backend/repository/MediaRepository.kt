package com.cms.backend.repository

import com.cms.backend.entity.Media
import com.cms.backend.entity.MediaType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MediaRepository : JpaRepository<Media, Long> {
    fun findByS3Key(s3Key: String): Optional<Media>
    fun findAllByMediaType(mediaType: MediaType, pageable: Pageable): Page<Media>
    fun findAllByUploadedById(uploadedById: Long, pageable: Pageable): Page<Media>
}





