package com.wall.student_crm.persistence.course

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository : JpaRepository<CourseEntity, Long> {

    @Query("SELECT COUNT(c) > 0 FROM CourseEntity c WHERE c.name = :name")
    fun existsByName(name: String): Boolean

    @Query("SELECT c FROM CourseEntity c WHERE c.name = :name")
    fun findByName(name: String): CourseEntity?
}