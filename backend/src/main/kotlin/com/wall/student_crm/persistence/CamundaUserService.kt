package com.wall.student_crm.persistence

import org.camunda.bpm.engine.IdentityService
import org.camunda.bpm.engine.identity.User
import org.springframework.stereotype.Service

@Service
class CamundaUserService(
    private val identityService: IdentityService
) {
    fun getUserIdByEmail(email: String): String? {
        val user: User? = identityService.createUserQuery()
            .userEmail(email)
            .singleResult()
        return user?.id
    }
}