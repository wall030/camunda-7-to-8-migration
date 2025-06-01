package com.wall.student_crm.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CamundaUserService(
   // private val identityService: IdentityService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(CamundaUserService::class.java)
    }

    fun getUserIdByEmail(email: String): String? {
        logger.info("Entering getUserIdByEmail with email={}", email)
        // ToDo
        return "test"
    }

    fun existsByEmail(email: String): Boolean {
        logger.info("Entering existsByEmail with email={}", email)
        // ToDo
       return true
    }
}