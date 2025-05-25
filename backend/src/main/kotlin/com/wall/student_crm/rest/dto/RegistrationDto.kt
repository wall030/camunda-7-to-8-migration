package com.wall.student_crm.rest.dto

data class RegistrationDto(
    val studentEmail: String,
    val prerequisiteA: Boolean,
    val prerequisiteB: Boolean,
    val prerequisiteC: Boolean,
    val prerequisiteD: Boolean,
    val course: String
)