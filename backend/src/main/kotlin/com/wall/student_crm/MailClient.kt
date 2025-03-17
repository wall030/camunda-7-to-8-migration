package com.wall.student_crm

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
    @Value("\${MAIL_HOST}")
    lateinit var host: String

    @Throws(MessagingException::class, UnsupportedEncodingException::class, MailException::class)
    fun sendEmail(email: String, subject: String, content: String) {
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        helper.setFrom(host)
        helper.setTo(email)
        helper.setSubject(subject)
        helper.setText(content, true)

        mailSender.send(message)
    }
}




