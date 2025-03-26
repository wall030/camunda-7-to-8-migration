package com.wall.student_crm.persistence.student

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.wall.student_crm.persistence.course.CourseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table


@Entity
@Table(name = "act_id_user")
class StudentEntity(
    @Id
    @Column(name = "id_")
    var id: String = "",

    @Column(name = "first_")
    var firstName: String = "",

    @Column(name = "last_")
    var lastName: String = "",

    @Column(name = "email_")
    var email: String = "",

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_course",
        joinColumns = [JoinColumn(name = "student_id")],
        inverseJoinColumns = [JoinColumn(name = "course_id")],
    )
    @JsonIgnoreProperties("students")
    var courses: MutableList<CourseEntity> = mutableListOf(),
) {
    constructor(firstName: String, lastName: String, email: String) : this() {
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
    }
}