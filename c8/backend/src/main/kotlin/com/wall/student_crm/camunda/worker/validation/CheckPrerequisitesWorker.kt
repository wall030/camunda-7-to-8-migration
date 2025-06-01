package com.wall.student_crm.camunda.worker.validation

import com.wall.student_crm.service.ValidationService
import io.camunda.zeebe.spring.client.annotation.JobWorker
import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError
import org.springframework.stereotype.Component

@Component
class CheckPrerequisitesWorker(
    private val validationService: ValidationService
) {

    @JobWorker(type = "check-prerequisites")
    fun handle(job: ActivatedJob): Map<String, Any> {
        val variables = job.variablesAsMap
        val prerequisite = variables["prerequisite"] as String
        val studentsPrerequisites = variables["studentsPrerequisites"] as List<String>

        if (validationService.isPrerequisiteMissing(prerequisite, studentsPrerequisites)) {
            throw ZeebeBpmnError("PREREQUISITES_NOT_MET", "Missing prerequisite found", emptyMap())
        }
        return emptyMap()
    }
}