package com.wall.student_crm.rest.dto.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateUserRequest(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    val username: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 4, message = "Password must be at least 4 characters")
    val password: String,

    @field:Size(max = 50, message = "First name must not exceed 50 characters")
    val firstName: String? = null,

    @field:Size(max = 50, message = "Last name must not exceed 50 characters")
    val lastName: String? = null,

    val groupIds: Set<Long> = emptySet()
)