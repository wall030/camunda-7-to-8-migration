package com.wall.student_crm.persistence.justification

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(name = "justifications")
class JustificationEntity(
    @Id
    @Column(name = "id", unique = true)
    val id: UUID = UUID.randomUUID(),
    @Column(name = "student_id", nullable = false)
    val studentId: String,
    @Column(name = "justification", nullable = false, columnDefinition = "TEXT")
    var justification: String,
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)