package com.wall.student_crm.persistence.course

import org.springframework.data.jpa.repository.JpaRepository

interface StudentCourseRepository : JpaRepository<StudentCourseEntity, StudentCourseId> {
    fun findByCourseId(courseId: Long): List<StudentCourseEntity>
}
