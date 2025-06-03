package com.wall.student_crm.rest.dto.task

data class CompleteTaskRequest(
    val variables: Map<String, Any> = emptyMap()
)