package com.wall.student_crm.camunda.worker

import com.wall.student_crm.service.business.StatusService
import com.wall.student_crm.shared.enums.Status
import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.springframework.stereotype.Component

@Component
class StatusWorker(
    private val statusService: StatusService
) {

    @JobWorker(type = "update-status")
    fun handle(job: ActivatedJob): Map<String, Any> {
        val variables = job.variablesAsMap
        val status = (variables["status"] as? String)?.let { Status.valueOf(it) } ?: Status.NOT_INITIALIZED
        val registrationAllowed = variables["registrationAllowed"] as? Boolean == true
        val cancelRegistration = variables["cancelRegistration"] as? Boolean == true
        val acceptJustification = variables["acceptJustification"] as? Boolean == true
        val overbooked = variables["overbooked"] as? Boolean == true

        val newStatus = statusService.determineStatus(
            currentStatus = status,
            registrationAllowed = registrationAllowed,
            cancelRegistration = cancelRegistration,
            acceptJustification = acceptJustification,
            overbooked = overbooked
        )

        return mapOf("status" to newStatus.name)
    }
}