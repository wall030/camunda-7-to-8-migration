package com.wall.student_crm.camunda.delegates.validation

import com.wall.student_crm.service.ValidationService
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CheckEnrollmentDelegate(
    private val validationService: ValidationService
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val studentEmail = execution.getVariable("studentEmail") as String
        val courseName = execution.getVariable("course") as String
        val isEnrolled = validationService.isEnrolled(studentEmail, courseName)

        if (isEnrolled) throw BpmnError("ALREADY_ENROLLED")
    }
}