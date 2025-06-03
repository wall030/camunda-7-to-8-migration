package com.wall.student_crm.camunda.worker.validation

import com.wall.student_crm.service.business.ValidationService
import io.camunda.zeebe.spring.client.annotation.JobWorker
import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError
import org.springframework.stereotype.Component

@Component
class CheckEmailFormatWorker(
    private val validationService: ValidationService
) {

    @JobWorker(type = "check-email-format")
    fun handle(job: ActivatedJob) {
        val variables = job.variablesAsMap
        val studentEmail = variables["studentEmail"] as String
        val isNotCorrectEmailFormat = !validationService.isCorrectEmailFormat(studentEmail)

        if (isNotCorrectEmailFormat) throw ZeebeBpmnError("INVALID_EMAIL_FORMAT", "Invalid email format", emptyMap())
    }
}