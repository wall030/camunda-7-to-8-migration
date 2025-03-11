package com.wall.student_crm.http.course

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CourseCreateUpdateDTO(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 1, max = 35, message = "Max 35 characters for name")
    val name: String = "",
)
