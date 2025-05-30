package com.wall.student_crm.service

import org.camunda.bpm.engine.IdentityService
import org.camunda.bpm.engine.identity.User
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CamundaUserService(
    private val identityService: IdentityService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(CamundaUserService::class.java)
    }

    fun getUserIdByEmail(email: String): String? {
        logger.info("Entering getUserIdByEmail with email={}", email)
        return try {
            val user: User? = identityService.createUserQuery()
                .userEmail(email)
                .singleResult()
            user?.id.also {
                logger.info("User ID {} found for email: {}", it ?: "none", email)
            }
        } catch (e: Exception) {
            logger.error("Failed to get user ID for email {}", email, e)
            null
        }
    }

    fun existsByEmail(email: String): Boolean {
        logger.info("Entering existsByEmail with email={}", email)
        return try {
            val exists = identityService.createUserQuery()
                .userEmail(email)
                .count() > 0
            logger.info("User exists for email {}: {}", email, exists)
            exists
        } catch (e: Exception) {
            logger.error("Failed to check user existence for email {}", email, e)
            false
        }
    }
}