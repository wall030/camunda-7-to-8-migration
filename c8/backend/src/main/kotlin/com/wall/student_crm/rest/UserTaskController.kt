package com.wall.student_crm.rest

import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class UserTaskController(
    private val taskService: TaskService,
    private val runtimeService: RuntimeService
) {

    @GetMapping
    fun getTasks(
        @RequestParam(required = false) assignee: String?,
        @RequestParam(required = false) processDefinitionKey: String?
    ): ResponseEntity<List<Map<String, String>>> {
        var query = taskService.createTaskQuery()

        if (!assignee.isNullOrBlank()) {
            query = query.taskAssignee(assignee)
        }
        if (!processDefinitionKey.isNullOrBlank()) {
            query = query.processDefinitionKey(processDefinitionKey)
        }

        val tasks = query.list().map {
            mapOf(
                "id" to it.id,
                "name" to it.name,
                "assignee" to (it.assignee ?: "unassigned"),
                "processInstanceId" to it.processInstanceId
            )
        }

        return ResponseEntity.ok(tasks)
    }

    @PostMapping("/{taskId}/claim")
    fun claimTask(
        @PathVariable taskId: String,
        @RequestParam assignee: String
    ): ResponseEntity<String> {
        taskService.claim(taskId, assignee)
        return ResponseEntity.ok("Task $taskId claimed by $assignee")
    }

    @PostMapping("/{taskId}/complete")
    fun completeTask(
        @PathVariable taskId: String,
        @RequestBody variables: Map<String, Any>
    ): ResponseEntity<String> {
        taskService.complete(taskId, variables)
        return ResponseEntity.ok("Task $taskId completed")
    }
}