package com.wall.student_crm.shared.mail

import com.wall.student_crm.shared.enums.MailMessageTemplate
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Component
class MailClient(
    private val mailSender: JavaMailSender
) {
    @Value("\${spring.mail.username}")
    private lateinit var senderMail: String

    private val logger: Logger = LoggerFactory.getLogger(MailClient::class.java)

    @Throws(MessagingException::class, MailException::class)
    fun sendEmail(email: String, template: MailMessageTemplate, qrCodeUrl: String = "") {
        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)

            helper.setFrom(senderMail)
            helper.setTo(email)
            helper.setSubject(template.subject)

            val content = when (template) {
                MailMessageTemplate.EXAM_CONFIRMATION_MESSAGE -> qrCodeUrl
                else -> template.content
            }

            helper.setText(content, true)

            mailSender.send(message)
            logger.info("Email sent successfully to $email with subject: ${template.subject}")
        } catch (e: Exception) {
            logger.error("Error sending email to $email: ${e.message}", e)
        }
    }
}