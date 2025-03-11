package com.wall.student_crm.http.course

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.wall.student_crm.http.student.StudentDTO
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CourseDTO(
    @field:NotBlank(message = "ID is required")
    val id: Long,
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 1, max = 35, message = "Max 35 characters for name")
    val name: String,
    @JsonIgnoreProperties("courses")
    val students: List<StudentDTO> = emptyList(),
)