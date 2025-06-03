package com.wall.student_crm.camunda.worker.validation

import com.wall.student_crm.service.business.ValidationService
import io.camunda.zeebe.spring.client.annotation.JobWorker
import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError
import org.springframework.stereotype.Component

@Component
class CheckStudentExistsWorker(
    private val validationService: ValidationService
) {

    @JobWorker(type = "check-student-exists")
    fun handle(job: ActivatedJob) {
        val variables = job.variablesAsMap
        val studentEmail = variables["studentEmail"] as String
        val studentNotExists = !validationService.studentExists(studentEmail)

        if (studentNotExists) throw ZeebeBpmnError("STUDENT_NOT_FOUND", "Student not found", emptyMap())
    }
}