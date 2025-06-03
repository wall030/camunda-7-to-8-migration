package com.wall.student_crm.service.business

import com.wall.student_crm.persistence.justification.JustificationEntity
import com.wall.student_crm.persistence.justification.JustificationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class JustificationService(
    private val userService: UserService,
    private val justificationRepository: JustificationRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(JustificationService::class.java)
    }

    @Transactional
    fun storeJustification(studentEmail: String, justificationText: String): UUID {
        logger.info("Entering storeJustification with studentEmail={}", studentEmail)
        val studentId = userService.findByEmail(studentEmail)!!.id

        // Check for existing justification (idempotence)
        justificationRepository.findByStudentIdAndJustification(studentId, justificationText)?.let {
            logger.info("Found existing justification with id={} for studentId={}", it.id, studentId)
            return it.id
        }

        val justification = JustificationEntity(
            studentId = studentId,
            justification = justificationText
        )
        val savedId = justificationRepository.save(justification).id
        logger.info("Saved new justification with id={} for studentId={}", savedId, studentId)
        return savedId
    }

    @Transactional
    fun removeJustification(justificationId: UUID) {
        logger.info("Entering removeJustification with justificationId={}", justificationId)
        justificationRepository.findById(justificationId).ifPresent { justification ->
            justificationRepository.delete(justification)
            logger.info("Deleted justification with id={}", justificationId)
        }
    }
}