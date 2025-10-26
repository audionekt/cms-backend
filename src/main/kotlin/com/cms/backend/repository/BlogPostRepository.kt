package com.cms.backend.repository

import com.cms.backend.entity.BlogPost
import com.cms.backend.entity.PostStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BlogPostRepository : JpaRepository<BlogPost, Long> {
    fun findBySlug(slug: String): Optional<BlogPost>
    
    fun findAllByStatus(status: PostStatus, pageable: Pageable): Page<BlogPost>
    
    fun findAllByAuthorId(authorId: Long, pageable: Pageable): Page<BlogPost>
    
    fun findAllByFeaturedTrue(pageable: Pageable): Page<BlogPost>
    
    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = :status ORDER BY bp.publishedAt DESC")
    fun findPublishedPosts(status: PostStatus, pageable: Pageable): Page<BlogPost>
    
    @Query("SELECT bp FROM BlogPost bp JOIN bp.tags t WHERE t.id = :tagId AND bp.status = :status")
    fun findByTagIdAndStatus(tagId: Long, status: PostStatus, pageable: Pageable): Page<BlogPost>
    
    @Query("SELECT bp FROM BlogPost bp WHERE (LOWER(bp.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(bp.excerpt) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND bp.status = :status")
    fun searchByTitleOrExcerpt(searchTerm: String, status: PostStatus, pageable: Pageable): Page<BlogPost>
}
