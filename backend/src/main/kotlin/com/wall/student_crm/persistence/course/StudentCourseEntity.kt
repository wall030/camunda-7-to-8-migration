package com.wall.student_crm.persistence.course

import jakarta.persistence.*

@Entity
@Table(name = "student_course")
@IdClass(StudentCourseId::class)
class StudentCourseEntity(
    @Id
    @Column(name = "student_id")
    var studentId: String = "",   // Camunda-User-ID

    @Id
    @Column(name = "course_id")
    var courseId: Long = 0L
)


data class StudentCourseId(
    var studentId: String = "",
    var courseId: Long = 0L
) : java.io.Serializable