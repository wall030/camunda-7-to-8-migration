package com.wall.student_crm.service.authorization

import com.wall.student_crm.rest.dto.task.UserTaskDto
import com.wall.student_crm.service.business.UserService
import com.wall.student_crm.service.integration.CamundaRestServiceWebClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserTaskService(
    private val camundaRestService: CamundaRestServiceWebClient,
    private val taskAuthorizationService: TaskAuthorizationService,
    private val userService: UserService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(UserTaskService::class.java)
    }

    fun getUserTasks(username: String, userRoles: List<String>): List<UserTaskDto> {
        val user = userService.findByUsername(username)
            ?: throw RuntimeException("User not found: $username")
        val userEmail = user.email

        logger.info("Fetching tasks for user: {} (Email: {}), Roles: {}", username, userEmail, userRoles)

        val tasks = when {
            userRoles.contains("ROLE_ADMIN") -> {
                logger.debug("Admin user detected - fetching all tasks")
                camundaRestService.getAllTasks()
            }

            userRoles.contains("ROLE_STUDENTS") -> {
                logger.debug("Student user detected - fetching assigned tasks for email: {}", userEmail)
                camundaRestService.getTasksByAssignee(userEmail)
            }

            userRoles.contains("ROLE_EXAMOFFICE") -> {
                logger.debug("Exam office user detected - fetching examoffice group tasks")
                camundaRestService.getTasksByCandidateGroup("examoffice")
            }

            userRoles.contains("ROLE_TECHNICALSERVICE") -> {
                logger.debug("Technical service user detected - fetching technicalservice group tasks")
                camundaRestService.getTasksByCandidateGroup("technicalservice")
            }

            else -> {
                logger.debug("No specific role matched - using authorization service")
                val allTasks = camundaRestService.getAllTasks()
                allTasks.filter { task ->
                    taskAuthorizationService.isUserAuthorizedForTask(username, task)
                }
            }
        }

        logger.info("Found {} tasks for user: {}", tasks.size, username)
        return tasks
    }

    fun getUserTask(taskId: String, username: String, userRoles: List<String>): UserTaskDto {
        logger.debug("Getting task {} for user: {}", taskId, username)
        val task = camundaRestService.getUserTask(taskId)

        if (!isUserAuthorizedForTask(task, username, userRoles)) {
            logger.warn("User {} not authorized to view task {}", username, taskId)
            throw SecurityException("User not authorized to view task $taskId")
        }

        logger.debug("User {} authorized to view task {}", username, taskId)
        return task
    }

    fun claimTask(taskId: String, assigneeOverride: String?, username: String, userRoles: List<String>) {
        logger.info("User {} attempting to claim task {}", username, taskId)
        val task = camundaRestService.getUserTask(taskId)

        if (!canUserClaimTask(task, username, userRoles)) {
            logger.warn("User {} not authorized to claim task {}", username, taskId)
            throw SecurityException("User not authorized to claim task $taskId")
        }

        val user = userService.findByUsername(username)
            ?: throw RuntimeException("User not found: $username")
        val assignee = assigneeOverride ?: user.email

        logger.info("Claiming task {} for assignee: {}", taskId, assignee)
        camundaRestService.claimTask(taskId, assignee)
        logger.info("Successfully claimed task {} for assignee: {}", taskId, assignee)
    }

    fun completeTask(taskId: String, variables: Map<String, Any>?, username: String, userRoles: List<String>) {
        logger.info("User {} attempting to complete task {} with variables: {}", username, taskId, variables)

        val task = camundaRestService.getUserTask(taskId)
        logger.debug("Found task: {}, assignee: {}, candidateGroups: {}", task.id, task.assignee, task.candidateGroups)

        if (!canUserCompleteTask(task, username, userRoles)) {
            logger.warn("User {} not authorized to complete task {}", username, taskId)
            throw SecurityException("User not authorized to complete task $taskId")
        }

        logger.info("User {} authorized - completing task {}", username, taskId)
        camundaRestService.completeTask(taskId, variables)
        logger.info("Successfully completed task {} by user {}", taskId, username)
    }

    private fun isUserAuthorizedForTask(task: UserTaskDto, username: String, userRoles: List<String>): Boolean {
        val user = userService.findByUsername(username) ?: return false
        val userEmail = user.email

        val authorized = when {
            userRoles.contains("ROLE_ADMIN") -> true
            userRoles.contains("ROLE_STUDENTS") -> task.assignee == userEmail
            userRoles.contains("ROLE_EXAMOFFICE") ->
                task.candidateGroups?.contains("examoffice") == true || task.assignee == userEmail

            userRoles.contains("ROLE_TECHNICALSERVICE") ->
                task.candidateGroups?.contains("technicalservice") == true || task.assignee == userEmail

            else -> taskAuthorizationService.isUserAuthorizedForTask(username, task)
        }

        logger.debug("Authorization check for task {}: user={}, authorized={}", task.id, username, authorized)
        return authorized
    }

    private fun canUserClaimTask(task: UserTaskDto, username: String, userRoles: List<String>): Boolean {
        val user = userService.findByUsername(username) ?: return false
        val userEmail = user.email

        val canClaim = when {
            userRoles.contains("ROLE_ADMIN") -> true
            userRoles.contains("ROLE_STUDENTS") ->
                task.assignee == userEmail || task.candidateGroups?.contains("students") == true

            userRoles.contains("ROLE_EXAMOFFICE") ->
                task.candidateGroups?.contains("examoffice") == true || task.assignee == userEmail

            userRoles.contains("ROLE_TECHNICALSERVICE") ->
                task.candidateGroups?.contains("technicalservice") == true || task.assignee == userEmail

            else -> taskAuthorizationService.isUserAuthorizedForTask(username, task)
        }

        logger.debug("Claim authorization check for task {}: user={}, canClaim={}", task.id, username, canClaim)
        return canClaim
    }

    private fun canUserCompleteTask(task: UserTaskDto, username: String, userRoles: List<String>): Boolean {
        val user = userService.findByUsername(username) ?: return false
        val userEmail = user.email

        val canComplete = when {
            userRoles.contains("ROLE_ADMIN") -> {
                logger.debug("Admin user {} can complete task {}", username, task.id)
                true
            }

            userRoles.contains("ROLE_EXAMOFFICE") -> {
                val authorized = task.candidateGroups?.contains("examoffice") == true || task.assignee == userEmail
                logger.debug(
                    "ExamOffice user {}: candidateGroups={}, canComplete={}",
                    username,
                    task.candidateGroups,
                    authorized
                )
                authorized
            }

            userRoles.contains("ROLE_TECHNICALSERVICE") -> {
                val authorized =
                    task.candidateGroups?.contains("technicalservice") == true || task.assignee == userEmail
                logger.debug(
                    "TechnicalService user {}: candidateGroups={}, canComplete={}",
                    username,
                    task.candidateGroups,
                    authorized
                )
                authorized
            }

            userRoles.contains("ROLE_STUDENTS") -> {
                val authorized = task.assignee == userEmail
                logger.debug(
                    "Student user {}: task.assignee={}, userEmail={}, canComplete={}",
                    username,
                    task.assignee,
                    userEmail,
                    authorized
                )
                authorized
            }

            else -> {
                val authorized = task.assignee == userEmail
                logger.debug(
                    "Other user {}: task.assignee={}, userEmail={}, authorized={}",
                    username,
                    task.assignee,
                    userEmail,
                    authorized
                )
                authorized
            }
        }

        return canComplete
    }
}