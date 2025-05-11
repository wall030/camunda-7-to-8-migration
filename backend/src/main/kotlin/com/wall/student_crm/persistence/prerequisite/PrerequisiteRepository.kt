package com.wall.student_crm.persistence.prerequisite

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PrerequisiteRepository : JpaRepository<PrerequisiteEntity, Long> {
    fun findByName(name: String): PrerequisiteEntity?
}