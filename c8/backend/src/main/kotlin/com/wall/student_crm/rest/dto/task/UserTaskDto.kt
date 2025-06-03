package com.wall.student_crm.rest.dto.task

data class UserTaskDto(
    val id: String,
    val taskDefinitionId: String? = null,
    val assignee: String? = null,
    val candidateUsers: List<String>? = null,
    val candidateGroups: List<String>? = null,
    val state: String? = null,
    val formKey: String? = null,
    val processInstanceId: String? = null,
    val processDefinitionId: String? = null,
    val variables: Map<String, Any>? = null
)