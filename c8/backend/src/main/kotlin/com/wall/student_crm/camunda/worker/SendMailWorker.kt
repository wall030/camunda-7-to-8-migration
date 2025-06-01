package com.wall.student_crm.camunda.worker

import com.wall.student_crm.shared.enums.MailType
import com.wall.student_crm.service.MailService
import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.springframework.stereotype.Component

@Component
class SendMailWorker(
    private val mailService: MailService
) {

    @JobWorker(type = "send-mail")
    fun handle(job: ActivatedJob) {
        val variables = job.variablesAsMap
        val mailTypeStr = variables["mailType"] as String
        val email = variables["studentEmail"] as String

        val mailType = MailType.valueOf(mailTypeStr)

        when (mailType) {
            MailType.EXAM_CONFIRMATION -> {
                val qrCodeUrl = variables["qrCodeUrl"] as String
                val courseName = variables["course"] as String
                mailService.sendConfirmation(email, qrCodeUrl, courseName)
            }
            MailType.EXAM_REJECTION -> mailService.sendRejection(email)
            MailType.ALREADY_ENROLLED -> mailService.sendAlreadyEnrolled(email)
            MailType.STUDENT_NOT_FOUND -> mailService.sendStudentNotFound(email)
            MailType.COURSE_NOT_FOUND -> mailService.sendCourseNotFound(email)
            MailType.PREREQUISITES_NOT_MET -> mailService.sendPrerequisitesNotMet(email)
        }
    }
}
