package com.wall.student_crm.camunda.worker

import com.wall.student_crm.service.business.JustificationService
import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.springframework.stereotype.Component

@Component
class StoreJustificationWorker(
    private val justificationService: JustificationService
) {

    @JobWorker(type = "store-justification")
    fun handle(job: ActivatedJob): Map<String, String> {
        val variables = job.variablesAsMap
        val studentEmail = variables["studentEmail"] as String
        val justificationText = variables["justification"] as String
        val justificationId = justificationService.storeJustification(studentEmail, justificationText)
        return mapOf("justificationId" to justificationId.toString())
    }
}