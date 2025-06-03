package com.wall.student_crm.rest.dto.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UpdateUserRequest(
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String? = null,

    @field:Email(message = "Email must be valid")
    val email: String? = null,

    @field:Size(max = 50, message = "First name must not exceed 50 characters")
    val firstName: String? = null,

    @field:Size(max = 50, message = "Last name must not exceed 50 characters")
    val lastName: String? = null,

    val groupIds: Set<Long>? = null
)