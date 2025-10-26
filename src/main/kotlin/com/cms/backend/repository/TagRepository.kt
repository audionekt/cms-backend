package com.cms.backend.repository

import com.cms.backend.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TagRepository : JpaRepository<Tag, Long> {
    fun findBySlug(slug: String): Optional<Tag>
    fun findByName(name: String): Optional<Tag>
    fun existsBySlug(slug: String): Boolean
    fun existsByName(name: String): Boolean
}





