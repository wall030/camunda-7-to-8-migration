package com.wall.student_crm.config

import jakarta.annotation.PostConstruct
import org.camunda.bpm.engine.ProcessEngine
import org.camunda.bpm.engine.authorization.Authorization
import org.camunda.bpm.engine.authorization.Permissions
import org.camunda.bpm.engine.authorization.Resources
import org.springframework.stereotype.Component

@Component
class AuthorizationSetup(
    private val processEngine: ProcessEngine
) {
    private val authorizationService = processEngine.authorizationService
    private val groups = listOf("examoffice", "students", "technicalservice")

    @PostConstruct
    fun initAuthorizations() {
        initApplicationAndFilterAuthorizations()
        initProcessDefinitionAuthorizations()
        initProcessInstanceAuthorizations()
    }

    private fun initApplicationAndFilterAuthorizations() {
        groups.forEach { groupId ->
            createAuthorizationIfNotExists(
                groupId = groupId,
                resourceType = Resources.APPLICATION,
                resourceId = "tasklist",
                permissions = listOf(Permissions.ALL)
            )

            createAuthorizationIfNotExists(
                groupId = groupId,
                resourceType = Resources.FILTER,
                resourceId = "*",
                permissions = listOf(Permissions.READ)
            )
        }
    }

    private fun initProcessDefinitionAuthorizations() {
        createAuthorizationIfNotExists(
            groupId = "students",
            resourceType = Resources.PROCESS_DEFINITION,
            resourceId = "examRegistration",
            permissions = listOf(Permissions.READ, Permissions.CREATE_INSTANCE)
        )

        createAuthorizationIfNotExists(
            groupId = "students",
            resourceType = Resources.PROCESS_DEFINITION,
            resourceId = "initialExistenceCheck",
            permissions = listOf(Permissions.READ)
        )

        createAuthorizationIfNotExists(
            groupId = "examoffice",
            resourceType = Resources.PROCESS_DEFINITION,
            resourceId = "examRegistration",
            permissions = listOf(Permissions.READ)
        )

        createAuthorizationIfNotExists(
            groupId = "technicalservice",
            resourceType = Resources.PROCESS_DEFINITION,
            resourceId = "examRegistration",
            permissions = listOf(Permissions.READ)
        )

        createAuthorizationIfNotExists(
            groupId = "examoffice",
            resourceType = Resources.PROCESS_DEFINITION,
            resourceId = "reviseCourseSize",
            permissions = listOf(Permissions.READ)
        )
    }

    private fun initProcessInstanceAuthorizations() {
        createAuthorizationIfNotExists(
            groupId = "students",
            resourceType = Resources.PROCESS_INSTANCE,
            resourceId = "*",
            permissions = listOf(Permissions.CREATE)
        )
    }

    private fun createAuthorizationIfNotExists(
        groupId: String,
        resourceType: Resources,
        resourceId: String,
        permissions: List<Permissions>
    ) {
        val count = authorizationService
            .createAuthorizationQuery()
            .groupIdIn(groupId)
            .resourceType(resourceType.resourceType())
            .resourceId(resourceId)
            .count()

        if (count == 0L) {
            val auth = authorizationService.createNewAuthorization(Authorization.AUTH_TYPE_GRANT)
            auth.groupId = groupId
            auth.setResource(resourceType)
            auth.resourceId = resourceId
            permissions.forEach { auth.addPermission(it) }
            authorizationService.saveAuthorization(auth)
        }
    }
}