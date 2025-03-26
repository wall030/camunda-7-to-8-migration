package com.wall.student_crm.camunda.delegates.validation

import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CheckEmailFormatDelegate : JavaDelegate {

    private val logger = LoggerFactory.getLogger(CheckEmailFormatDelegate::class.java)

    override fun execute(execution: DelegateExecution) {
        val studentEmail = execution.getVariable("studentEmail").toString()

        if (!isValidEmail(studentEmail)) {
            logger.error("Invalid email format detected: $studentEmail")
            execution.setVariable("error", true)
            throw BpmnError("INVALID_EMAIL", "Invalid Email Format")
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(regex)
    }
}