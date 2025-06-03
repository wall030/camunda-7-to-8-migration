package com.wall.student_crm.rest.dto.task

data class CamundaTaskResponse(
    val id: String,
    val name: String? = null,
    val taskDefinitionId: String? = null,
    val processName: String? = null,
    val creationDate: String? = null,
    val completionDate: String? = null,
    val assignee: String? = null,
    val taskState: String? = null,
    val sortValues: List<String>? = null,
    val isFirst: Boolean? = null,
    val formKey: String? = null,
    val formId: String? = null,
    val formVersion: Int? = null,
    val isFormEmbedded: Boolean? = null,
    val processDefinitionKey: String? = null,
    val processInstanceKey: String? = null,
    val tenantId: String? = null,
    val dueDate: String? = null,
    val followUpDate: String? = null,
    val candidateGroups: List<String>? = null,
    val candidateUsers: List<String>? = null,
    val variables: List<Map<String, Any>>? = null,
    val context: Any? = null,
    val implementation: String? = null,
    val priority: Int? = null
) {
    fun toUserTaskDto(): UserTaskDto {
        val variablesMap = variables?.associate { variable ->
            val name = variable["name"] as? String ?: ""
            val value = variable["value"] ?: ""
            name to value
        }

        return UserTaskDto(
            id = id,
            taskDefinitionId = taskDefinitionId,
            assignee = assignee,
            candidateUsers = candidateUsers,
            candidateGroups = candidateGroups,
            state = taskState,
            formKey = formKey,
            processInstanceId = processInstanceKey,
            processDefinitionId = processDefinitionKey,
            variables = variablesMap
        )
    }
}