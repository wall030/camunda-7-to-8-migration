package com.wall.student_crm.persistence.justification

import com.wall.student_crm.persistence.student.StudentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JustificationRepository : JpaRepository<JustificationEntity, UUID> {
}