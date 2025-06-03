package com.wall.student_crm.service.integration

import com.wall.student_crm.config.CamundaConfig
import com.wall.student_crm.rest.dto.task.CamundaTaskResponse
import com.wall.student_crm.rest.dto.task.UserTaskDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

// Replace "Tasklist REST API"s query task/s with new "Camunda REST API" when its no alpha feature anymore
@Service
class CamundaRestServiceWebClient(
    private val tokenService: CamundaTokenService,
    private val camundaConfig: CamundaConfig
) {

    companion object {
        private val logger = LoggerFactory.getLogger(CamundaRestServiceWebClient::class.java)
    }

    private val webClient = WebClient.builder()
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    fun getAllTasks(): List<UserTaskDto> {
        val url = "${camundaConfig.tasklistApiUrl}/v1/tasks/search"
        logger.debug("Fetching all tasks from Camunda: {}", url)

        return try {
            val requestBody = mapOf("state" to "CREATED")

            val response = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenService.getAccessToken()}")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono<Array<CamundaTaskResponse>>()
                .block() ?: emptyArray()

            logger.info("Successfully fetched {} tasks from Camunda", response.size)
            response.map { it.toUserTaskDto() }
        } catch (e: Exception) {
            logger.error("Error fetching all tasks from Camunda: {}", e.message, e)
            emptyList()
        }
    }

    fun getUserTask(taskId: String): UserTaskDto {
        val url = "${camundaConfig.tasklistApiUrl}/v1/tasks/$taskId"
        logger.debug("Fetching task {} from Camunda", taskId)

        return try {
            val response = webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenService.getAccessToken()}")
                .retrieve()
                .bodyToMono<CamundaTaskResponse>()
                .block() ?: throw RuntimeException("Task not found with id: $taskId")

            logger.debug("Successfully fetched task: {}", taskId)
            response.toUserTaskDto()
        } catch (e: Exception) {
            logger.error("Error fetching task {}: {}", taskId, e.message, e)
            throw e
        }
    }

    fun claimTask(taskId: String, assignee: String) {
        val url = "${camundaConfig.tasklistApiUrl}/v1/tasks/$taskId/assign"
        logger.info("Claiming task {} for assignee: {}", taskId, assignee)

        val requestBody = mapOf(
            "assignee" to assignee,
            "allowOverrideAssignment" to true
        )

        try {
            webClient.patch()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenService.getAccessToken()}")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono<Void>()
                .block()

            logger.info("Successfully claimed task {} for assignee: {}", taskId, assignee)
        } catch (e: Exception) {
            logger.error("Error claiming task {} for assignee {}: {}", taskId, assignee, e.message, e)
            throw e
        }
    }

    fun completeTask(taskId: String, variables: Map<String, Any>?) {
        val url = "${camundaConfig.restApiUrl}/v1/user-tasks/$taskId/completion"
        logger.info("Completing task {} with variables: {}", taskId, variables)

        val requestBody = if (variables?.isNotEmpty() == true) {
            mapOf("variables" to variables)
        } else {
            mapOf("variables" to emptyMap<String, Any>())
        }

        logger.debug("Zeebe REST API URL: {}", url)
        logger.debug("Request body: {}", requestBody)

        try {
            val zeebeToken = tokenService.getZeebeAccessToken()

            val response = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $zeebeToken")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono<String>()
                .block()

            logger.info("Successfully completed task {}", taskId)
            logger.debug("Completion response: {}", response)
        } catch (e: Exception) {
            logger.error("Error completing task {}: {}", taskId, e.message, e)
            throw e
        }
    }

    fun getTasksByAssignee(assignee: String): List<UserTaskDto> {
        val url = "${camundaConfig.tasklistApiUrl}/v1/tasks/search"
        logger.debug("Fetching tasks for assignee: {}", assignee)

        val requestBody = mapOf(
            "state" to "CREATED",
            "assigned" to true,
            "assignee" to assignee
        )

        return try {
            val response = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenService.getAccessToken()}")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono<Array<CamundaTaskResponse>>()
                .block() ?: emptyArray()

            logger.info("Found {} tasks for assignee: {}", response.size, assignee)
            response.map { it.toUserTaskDto() }
        } catch (e: Exception) {
            logger.error("Error fetching tasks for assignee {}: {}", assignee, e.message, e)
            emptyList()
        }
    }

    fun getTasksByCandidateGroup(candidateGroup: String): List<UserTaskDto> {
        val url = "${camundaConfig.tasklistApiUrl}/v1/tasks/search"
        logger.debug("Fetching tasks for candidate group: {}", candidateGroup)

        val unassignedRequestBody = mapOf(
            "state" to "CREATED",
            "assigned" to false,
            "candidateGroup" to candidateGroup
        )

        return try {
            // Fetch unassigned tasks for the candidate group
            val unassignedResponse = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenService.getAccessToken()}")
                .bodyValue(unassignedRequestBody)
                .retrieve()
                .bodyToMono<Array<CamundaTaskResponse>>()
                .block() ?: emptyArray()

            val unassignedTasks = unassignedResponse.map { it.toUserTaskDto() }
            logger.debug("Found {} unassigned tasks for candidate group: {}", unassignedTasks.size, candidateGroup)

            // Fetch assigned tasks and filter by candidate group
            val assignedRequestBody = mapOf(
                "state" to "CREATED",
                "assigned" to true
            )

            val assignedResponse = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenService.getAccessToken()}")
                .bodyValue(assignedRequestBody)
                .retrieve()
                .bodyToMono<Array<CamundaTaskResponse>>()
                .block() ?: emptyArray()

            val assignedTasks = assignedResponse.map { it.toUserTaskDto() }
                .filter { task -> task.candidateGroups?.contains(candidateGroup) == true }

            logger.debug("Found {} assigned tasks for candidate group: {}", assignedTasks.size, candidateGroup)

            val allTasks = (unassignedTasks + assignedTasks).distinctBy { it.id }
            logger.info("Total {} tasks found for candidate group: {}", allTasks.size, candidateGroup)

            allTasks
        } catch (e: Exception) {
            logger.error("Error fetching tasks for candidate group {}: {}", candidateGroup, e.message, e)
            emptyList()
        }
    }
}