package com.wall.student_crm.persistence.course

import com.wall.student_crm.persistence.prerequisite.PrerequisiteEntity
import jakarta.persistence.*


@Entity
@Table(name = "course")
class CourseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0L,

    @Column(name = "name", unique = true)
    var name: String = "",

    @Column(name = "max_size")
    var maxSize: Int = 10,

    @Column(name = "current_size")
    var currentSize: Int = 0,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "course_prerequisite",
        joinColumns = [JoinColumn(name = "course_id")],
        inverseJoinColumns = [JoinColumn(name = "prerequisite_id")]
    )
    var prerequisites: MutableList<PrerequisiteEntity> = mutableListOf()
) {
    constructor(name: String) : this() {
        this.name = name
    }
}