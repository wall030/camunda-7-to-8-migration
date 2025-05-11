package com.wall.student_crm.persistence.course

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.wall.student_crm.persistence.prerequisite.PrerequisiteEntity
import com.wall.student_crm.persistence.student.StudentEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table


@Entity
@Table(name = "course")
class CourseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    var id: Long = 0L,

    @Column(name = "name", unique = true)
    var name: String = "",

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("courses")
    var students: MutableList<StudentEntity> = mutableListOf(),

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