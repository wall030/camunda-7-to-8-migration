package com.wall.student_crm.persistence.course

import jakarta.persistence.*

@Entity
@Table(name = "student_course")
@IdClass(StudentCourseId::class)
class StudentCourseEntity(
    @Id
    @Column(name = "student_id")
    var studentId: Long? = 0L,

    @Id
    @Column(name = "course_id")
    var courseId: Long = 0L
)


data class StudentCourseId(
    var studentId: Long? = 0L,
    var courseId: Long = 0L
) : java.io.Serializable