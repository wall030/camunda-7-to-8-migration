package com.wall.student_crm.camunda.listeners

import com.wall.student_crm.shared.enums.Status
import com.wall.student_crm.shared.enums.Status.*
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.ExecutionListener
import org.springframework.stereotype.Component

@Component
class StatusListener : ExecutionListener {
    override fun notify(execution: DelegateExecution) {

        val status = execution.getVariable("status") as? Status ?: CHECKING
        val registrationAllowed = execution.getVariable("registrationAllowed") as? Boolean == true
        val cancelRegistration = execution.getVariable("cancelRegistration") as? Boolean == true
        val acceptJustification = execution.getVariable("acceptJustification") as? Boolean == true
        val isError = execution.getVariable("error") as? Boolean == true

        val newStatus = determineNewStatus(status, registrationAllowed, cancelRegistration, acceptJustification, isError)

        execution.setVariable("status", newStatus.name)
    }

    private fun determineNewStatus(
        currentStatus: Status,
        registrationAllowed: Boolean,
        cancelRegistration: Boolean,
        acceptJustification: Boolean,
        isError: Boolean
    ): Status {
        return when (currentStatus) {
            CHECKING -> if (registrationAllowed && !isError) ENROLLING else JUSTIFYING
            JUSTIFYING -> if (cancelRegistration) STOPPED else EXAM_OFFICE_CHECKING
            EXAM_OFFICE_CHECKING -> if (acceptJustification) ENROLLING else REJECTED
            ENROLLING -> SUCCESSFUL
            else -> CHECKING
        }
    }
}
