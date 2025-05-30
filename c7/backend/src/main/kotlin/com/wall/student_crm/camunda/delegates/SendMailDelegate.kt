package com.wall.student_crm.camunda.delegates

import com.wall.student_crm.shared.enums.MailType
import com.wall.student_crm.service.MailService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class SendMailDelegate(
    private val mailService: MailService
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val mailTypeStr = execution.getVariable("mailType") as String
        val email = execution.getVariable("studentEmail") as String

        val mailType = MailType.valueOf(mailTypeStr)

        when (mailType) {
            MailType.EXAM_CONFIRMATION -> {
                val qrCodeUrl = execution.getVariable("qrCodeUrl") as String
                val courseName = execution.getVariable("course") as String

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
