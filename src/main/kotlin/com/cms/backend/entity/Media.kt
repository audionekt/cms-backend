package com.cms.backend.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "media")
data class Media(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "file_name", nullable = false)
    val fileName: String,

    @Column(name = "original_file_name", nullable = false)
    val originalFileName: String,

    @Column(name = "file_url", nullable = false)
    val fileUrl: String,

    @Column(name = "s3_key", nullable = false)
    val s3Key: String,

    @Column(name = "content_type", nullable = false)
    val contentType: String,

    @Column(name = "file_size", nullable = false)
    val fileSize: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    val mediaType: MediaType,

    val width: Int? = null,

    val height: Int? = null,

    @Column(name = "alt_text")
    val altText: String? = null,

    @Column(name = "caption", length = 500)
    val caption: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id")
    val uploadedBy: User? = null,

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    val uploadedAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class MediaType {
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT,
    OTHER
}



