package com.wall.student_crm.persistence.justification

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface JustificationRepository : JpaRepository<JustificationEntity, UUID> {
}