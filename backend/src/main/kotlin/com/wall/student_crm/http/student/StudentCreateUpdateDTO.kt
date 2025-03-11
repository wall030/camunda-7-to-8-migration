package com.wall.student_crm.http.student

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class StudentCreateUpdateDTO(
    @field:NotBlank(message = "First name is required")
    @field:Size(min = 1, max = 20, message = "Max 20 characters for first name")
    val firstName: String,
    @field:NotBlank(message = "Last name is required")
    @field:Size(min = 1, max = 20, message = "Max 20 characters for last name")
    val lastName: String,
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,
)


