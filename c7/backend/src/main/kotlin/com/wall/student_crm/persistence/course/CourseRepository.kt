package com.wall.student_crm.persistence.course

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository : JpaRepository<CourseEntity, Long> {
    fun findByName(name: String): CourseEntity?
}