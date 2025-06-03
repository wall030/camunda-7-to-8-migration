package com.wall.student_crm.camunda.worker.validation

import com.wall.student_crm.service.business.ValidationService
import io.camunda.zeebe.spring.client.annotation.JobWorker
import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError
import org.springframework.stereotype.Component

@Component
class CheckEnrollmentWorker(
    private val validationService: ValidationService
) {

    @JobWorker(type = "check-enrollment")
    fun handle(job: ActivatedJob) {
        val variables = job.variablesAsMap
        val studentEmail = variables["studentEmail"] as String
        val courseName = variables["course"] as String
        val isEnrolled = validationService.isEnrolled(studentEmail, courseName)

        if (isEnrolled) throw ZeebeBpmnError("ALREADY_ENROLLED", "Student already Enrolled", emptyMap())
    }
}