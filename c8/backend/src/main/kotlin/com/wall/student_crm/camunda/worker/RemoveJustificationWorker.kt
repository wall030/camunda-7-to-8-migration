package com.wall.student_crm.camunda.worker

import com.wall.student_crm.service.business.JustificationService
import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RemoveJustificationWorker(
    private val justificationService: JustificationService
) {

    @JobWorker(type = "remove-justification")
    fun handle(job: ActivatedJob): Map<String, Any?> {
        val variables = job.variablesAsMap
        val justificationId = UUID.fromString(variables["justificationId"] as String)
        justificationService.removeJustification(justificationId)
        return mapOf("justification" to null)
    }
}