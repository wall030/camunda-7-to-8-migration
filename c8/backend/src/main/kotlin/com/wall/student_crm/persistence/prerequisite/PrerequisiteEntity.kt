package com.wall.student_crm.persistence.prerequisite

import jakarta.persistence.*

@Entity
@Table(name = "prerequisites")
class PrerequisiteEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String = ""
)
