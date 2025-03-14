package com.wall.student_crm.persistence.student

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : JpaRepository<StudentEntity, String> {
    fun findByEmail(email: String): StudentEntity?
}