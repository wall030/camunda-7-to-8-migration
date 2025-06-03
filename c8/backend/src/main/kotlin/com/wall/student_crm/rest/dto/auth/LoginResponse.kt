package com.wall.student_crm.rest.dto.auth

import com.wall.student_crm.rest.dto.user.UserDto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: UserDto
)