package com.wall.student_crm.rest.controllers

import com.wall.student_crm.rest.dto.task.ClaimTaskRequest
import com.wall.student_crm.rest.dto.task.CompleteTaskRequest
import com.wall.student_crm.rest.dto.task.UserTaskDto
import com.wall.student_crm.service.authorization.UserTaskService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tasks")
@PreAuthorize("hasRole('USER')")
class UserTaskController(
    private val userTaskService: UserTaskService
) {

    @GetMapping
    fun getUserTasks(authentication: Authentication): ResponseEntity<List<UserTaskDto>> {
        return try {
            val userRoles = authentication.authorities.map { it.authority }
            val tasks = userTaskService.getUserTasks(authentication.name, userRoles)
            ResponseEntity.ok(tasks)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/{taskId}")
    fun getUserTask(
        @PathVariable taskId: String,
        authentication: Authentication
    ): ResponseEntity<UserTaskDto> {
        return try {
            val userRoles = authentication.authorities.map { it.authority }
            val task = userTaskService.getUserTask(taskId, authentication.name, userRoles)
            ResponseEntity.ok(task)
        } catch (e: SecurityException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping("/{taskId}/claim")
    fun claimTask(
        @PathVariable taskId: String,
        @RequestBody request: ClaimTaskRequest,
        authentication: Authentication
    ): ResponseEntity<Void> {
        return try {
            val userRoles = authentication.authorities.map { it.authority }
            userTaskService.claimTask(taskId, request.assignee, authentication.name, userRoles)
            ResponseEntity.ok().build()
        } catch (e: SecurityException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping("/{taskId}/complete")
    fun completeTask(
        @PathVariable taskId: String,
        @RequestBody request: CompleteTaskRequest,
        authentication: Authentication
    ): ResponseEntity<Void> {
        return try {
            val userRoles = authentication.authorities.map { it.authority }
            userTaskService.completeTask(taskId, request.variables, authentication.name, userRoles)
            ResponseEntity.ok().build()
        } catch (e: SecurityException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}