package com.cms.backend.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "blog_posts")
data class BlogPost(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Column(unique = true, nullable = false)
    val slug: String,

    @Column(length = 500)
    val excerpt: String? = null,

    // The MDX content - this is where React components can be embedded
    @Column(columnDefinition = "TEXT", nullable = false)
    val mdxContent: String,

    val featuredImageUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "featured_media_id")
    val featuredMedia: Media? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    val author: User,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "blog_post_tags",
        joinColumns = [JoinColumn(name = "blog_post_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    val tags: MutableList<Tag> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: PostStatus = PostStatus.DRAFT,

    // SEO metadata
    val metaTitle: String? = null,

    @Column(length = 500)
    val metaDescription: String? = null,

    val metaKeywords: String? = null,

    // Publishing
    val publishedAt: LocalDateTime? = null,

    val scheduledAt: LocalDateTime? = null,

    // View count for analytics
    @Column(nullable = false)
    val viewCount: Long = 0,

    // Reading time in minutes (can be calculated from content)
    val readingTimeMinutes: Int? = null,

    // Allow comments
    @Column(nullable = false)
    val allowComments: Boolean = true,

    // Featured post flag
    @Column(nullable = false)
    val featured: Boolean = false,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class PostStatus {
    DRAFT,
    PUBLISHED,
    SCHEDULED,
    ARCHIVED
}
