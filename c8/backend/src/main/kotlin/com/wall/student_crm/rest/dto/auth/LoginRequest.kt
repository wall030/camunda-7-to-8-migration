package com.wall.student_crm.rest.dto.auth

data class LoginRequest(
    val username: String,
    val password: String
)