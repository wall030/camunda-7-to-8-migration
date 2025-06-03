package com.wall.student_crm.service.integration

import com.wall.student_crm.shared.enums.MailMessageTemplate
import com.wall.student_crm.shared.MailClient
import org.springframework.stereotype.Service

@Service
class MailService(
    private val client: MailClient
) {

    fun sendConfirmation(email: String, qrCodeUrl: String, courseName: String) {
        val qrCodeHtml = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Registration Confirmation: $courseName</title>
            </head>
            <body style="font-family: Arial, sans-serif; color: #333; padding: 20px;">
                <p>We would like to confirm your registration for exam $courseName.</p>
                <p>To add this event to your calendar, you can scan the QR code below:</p>
                <img src="$qrCodeUrl" alt="Exam event QR Code">
                <p>Best regards :)</p>
            </body>
            </html>
        """.trimIndent().replace("\n", "").replace("\r", "")

        client.sendEmail(email, MailMessageTemplate.EXAM_CONFIRMATION_MESSAGE, qrCodeHtml)
    }

    fun sendRejection(email: String) {
        client.sendEmail(email, MailMessageTemplate.EXAM_REJECTION_MESSAGE)
    }

    fun sendAlreadyEnrolled(email: String) {
        client.sendEmail(email, MailMessageTemplate.ALREADY_ENROLLED_MESSAGE)
    }

    fun sendStudentNotFound(email: String) {
        client.sendEmail(email, MailMessageTemplate.STUDENT_NOT_FOUND_MESSAGE)
    }

    fun sendCourseNotFound(email: String) {
        client.sendEmail(email, MailMessageTemplate.COURSE_NOT_FOUND_MESSAGE)
    }

    fun sendPrerequisitesNotMet(email: String) {
        client.sendEmail(email, MailMessageTemplate.PREREQUISITES_NOT_MET)
    }
}