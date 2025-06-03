package com.wall.student_crm.rest.dto

import com.wall.student_crm.persistence.group.GroupEntity
import com.wall.student_crm.persistence.user.UserEntity
import com.wall.student_crm.rest.dto.group.GroupDto
import com.wall.student_crm.rest.dto.user.UserDto

fun UserEntity.toDto(): UserDto {
    return UserDto(
        id = this.id,
        username = this.username,
        email = this.email,
        firstName = this.firstName,
        lastName = this.lastName,
        groups = this.groups.map { it.toDto() }
    )
}

fun GroupEntity.toDto(): GroupDto {
    return GroupDto(
        id = this.id,
        name = this.name,
        description = this.description
    )
}