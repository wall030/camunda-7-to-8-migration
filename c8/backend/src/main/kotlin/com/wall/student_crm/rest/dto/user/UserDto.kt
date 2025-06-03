package com.wall.student_crm.rest.dto.user

import com.wall.student_crm.rest.dto.group.GroupDto

data class UserDto(
    val id: Long?,
    val username: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val groups: List<GroupDto>
)