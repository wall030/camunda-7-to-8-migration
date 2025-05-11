package com.wall.student_crm.shared.mail

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.stereotype.Service
import com.wall.student_crm.shared.enums.MailMessageTemplate.EXAM_CONFIRMATION_MESSAGE
import com.wall.student_crm.shared.enums.MailMessageTemplate.EXAM_REJECTION_MESSAGE
import com.wall.student_crm.shared.enums.MailMessageTemplate.ALREADY_ENROLLED_MESSAGE
import com.wall.student_crm.shared.enums.MailMessageTemplate.STUDENT_NOT_FOUND_MESSAGE
import com.wall.student_crm.shared.enums.MailMessageTemplate.COURSE_NOT_FOUND_MESSAGE
import com.wall.student_crm.shared.enums.MailMessageTemplate.PREREQUISITES_NOT_MET

@Service
class MailService(
    private val client: MailClient
) {

    fun sendConfirmation(execution: DelegateExecution) {
        val qrCodeUrl = execution.getVariable("qrCodeUrl") as String
        val courseName = execution.getVariable("course") as String
        val qrCode = """
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Registration Confirmation: ${courseName}</title>
                        </head>
                        <body style="font-family: Arial, sans-serif; color: #333; padding: 20px;">
                            <p>We would like to confirm your registration for exam ${courseName}.</p>
                            <p>To add this event to your calendar, you can scan the QR code below:</p>
                            <img src=${qrCodeUrl} alt="Exam event QR Code">
                            <p>Best regards :)</p>
                        </body>
                        </html>
                    """.trimIndent().replace("\n", "").replace("\r", "")
        client.sendEmail(email(execution), EXAM_CONFIRMATION_MESSAGE, qrCode)
    }

    fun sendRejection(execution: DelegateExecution) {
        client.sendEmail(email(execution), EXAM_REJECTION_MESSAGE)
    }

    fun sendAlreadyEnrolled(execution: DelegateExecution) {
        client.sendEmail(email(execution), ALREADY_ENROLLED_MESSAGE)
    }

    fun sendStudentNotFound(execution: DelegateExecution) {
        client.sendEmail(email(execution), STUDENT_NOT_FOUND_MESSAGE)
    }

    fun sendCourseNotFound(execution: DelegateExecution) {
        client.sendEmail(email(execution), COURSE_NOT_FOUND_MESSAGE)
    }

    fun sendPrerequisitesNotMet(execution: DelegateExecution) {
        client.sendEmail(email(execution), PREREQUISITES_NOT_MET)
    }

    private fun email(execution: DelegateExecution): String {
        return execution.getVariable("studentEmail").toString()
    }
}