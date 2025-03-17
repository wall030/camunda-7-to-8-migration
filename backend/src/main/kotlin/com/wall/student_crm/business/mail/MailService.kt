package com.wall.student_crm.business.mail

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.stereotype.Service
import com.wall.student_crm.enums.MailMessageTemplate.EXAM_CONFIRMATION_MESSAGE
import com.wall.student_crm.enums.MailMessageTemplate.EXAM_REJECTION_MESSAGE

@Service
class MailService(
    private val client: MailClient
) {

    fun sendConfirmation(execution: DelegateExecution) {
        val email = execution.getVariable("studentEmail").toString()
        client.sendEmail(email, EXAM_CONFIRMATION_MESSAGE)
    }

    fun sendRejection(execution: DelegateExecution) {
        val email = execution.getVariable("studentEmail").toString()
        client.sendEmail(email, EXAM_REJECTION_MESSAGE)
    }
}