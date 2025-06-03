package com.wall.student_crm.service.authorization

import com.wall.student_crm.persistence.user.UserRepository
import com.wall.student_crm.rest.dto.task.UserTaskDto
import org.springframework.stereotype.Service

@Service
class TaskAuthorizationService(
    private val userRepository: UserRepository
) {

    fun isUserAuthorizedForTask(username: String, task: UserTaskDto): Boolean {
        val user = userRepository.findByUsername(username)
            .orElseThrow { RuntimeException("User not found: $username") }

        println("Checking authorization for user: $username, task: ${task.id}")
        println("Task assignee: ${task.assignee}")
        println("Task candidateUsers: ${task.candidateUsers}")
        println("Task candidateGroups: ${task.candidateGroups}")
        println("User groups: ${user.groups.map { it.name }}")

        // Prüfe ob User bereits assignee ist
        if (user.username == task.assignee) {
            println("User is assignee - authorized")
            return true
        }

        // Prüfe ob User in candidateUsers ist
        if (task.candidateUsers?.contains(user.username) == true) {
            println("User is in candidateUsers - authorized")
            return true
        }

        // Prüfe ob User zu einer candidateGroup gehört
        if (!task.candidateGroups.isNullOrEmpty()) {
            val userGroupNames = user.groups.map { it.name }.toSet()
            val hasMatchingGroup = task.candidateGroups.any { candidateGroup ->
                val matches = candidateGroup in userGroupNames
                if (matches) {
                    println("User group '$candidateGroup' matches task candidate group - authorized")
                }
                matches
            }
            if (hasMatchingGroup) {
                return true
            }
        }

        println("User not authorized for task")
        return false
    }
}