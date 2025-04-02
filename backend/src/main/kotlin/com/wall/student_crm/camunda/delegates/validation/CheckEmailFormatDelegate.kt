package com.wall.student_crm.camunda.delegates.validation

import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CheckEmailFormatDelegate : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val studentEmail = execution.getVariable("studentEmail").toString()

        if (!isValidEmail(studentEmail)) {
            throw BpmnError("INVALID_EMAIL_FORMAT")
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(regex)
    }
}