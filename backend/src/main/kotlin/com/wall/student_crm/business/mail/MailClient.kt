package com.wall.student_crm.business.mail

import com.wall.student_crm.enums.MailMessageTemplate
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.io.UnsupportedEncodingException
import org.springframework.beans.factory.annotation.Value

@Component
class MailClient (
    private val mailSender: JavaMailSender
) {
    @Value("\${spring.mail.username}")
    lateinit var senderMail: String

    @Throws(MessagingException::class, UnsupportedEncodingException::class, MailException::class)
    fun sendEmail(email: String, template: MailMessageTemplate) {
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        helper.setFrom(senderMail)
        helper.setTo(email)
        helper.setSubject(template.subject)
        helper.setText(template.content, true)

        mailSender.send(message)
    }
}




