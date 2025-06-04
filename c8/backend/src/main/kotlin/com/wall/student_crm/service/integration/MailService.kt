package com.wall.student_crm.service.integration

import com.wall.student_crm.shared.enums.MailMessageTemplate
import com.wall.student_crm.shared.MailClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MailService(
    private val client: MailClient
) {

    // Simple in-memory cache (idempotence))
    private val sentMails = mutableSetOf<String>()

    fun sendConfirmation(email: String, qrCodeUrl: String, courseName: String) {
        val mailKey = "CONFIRMATION:$email:$courseName:${qrCodeUrl.hashCode()}"

        if (sentMails.contains(mailKey)) {
            logger.info("Mail already sent to {} for course {}", email, courseName)
            return
        }

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
        sentMails.add(mailKey)
        logger.info("Sent confirmation mail to {} for course {}", email, courseName)
    }

    fun sendRejection(email: String) {
        val mailKey = "REJECTION:$email"
        if (sentMails.contains(mailKey)) {
            logger.info("Rejection mail already sent to {}", email)
            return
        }

        client.sendEmail(email, MailMessageTemplate.EXAM_REJECTION_MESSAGE)
        sentMails.add(mailKey)
    }

    fun sendAlreadyEnrolled(email: String) {
        val mailKey = "ALREADY_ENROLLED:$email"
        if (sentMails.contains(mailKey)) return

        client.sendEmail(email, MailMessageTemplate.ALREADY_ENROLLED_MESSAGE)
        sentMails.add(mailKey)
    }

    fun sendStudentNotFound(email: String) {
        val mailKey = "STUDENT_NOT_FOUND:$email"
        if (sentMails.contains(mailKey)) return

        client.sendEmail(email, MailMessageTemplate.STUDENT_NOT_FOUND_MESSAGE)
        sentMails.add(mailKey)
    }

    fun sendCourseNotFound(email: String) {
        val mailKey = "COURSE_NOT_FOUND:$email"
        if (sentMails.contains(mailKey)) return

        client.sendEmail(email, MailMessageTemplate.COURSE_NOT_FOUND_MESSAGE)
        sentMails.add(mailKey)
    }

    fun sendPrerequisitesNotMet(email: String) {
        val mailKey = "PREREQUISITES_NOT_MET:$email"
        if (sentMails.contains(mailKey)) return

        client.sendEmail(email, MailMessageTemplate.PREREQUISITES_NOT_MET)
        sentMails.add(mailKey)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MailService::class.java)
    }
}