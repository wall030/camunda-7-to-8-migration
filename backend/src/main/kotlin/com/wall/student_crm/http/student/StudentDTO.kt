package com.wall.student_crm.http.student

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.wall.student_crm.http.course.CourseDTO
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class StudentDTO(
    @field:NotBlank(message = "ID is required")
    val id: String,
    val firstName: String,
    val lastName: String,
    @field:Email(message = "Invalid email format")
    val email: String,
    @JsonIgnoreProperties("students")
    val courses: List<CourseDTO> = emptyList(),
)