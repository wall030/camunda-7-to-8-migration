package com.wall.student_crm.camunda.delegates.validation

import com.wall.student_crm.service.ValidationService
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CheckStudentExistsDelegate(
    private val validationService: ValidationService
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val studentEmail = execution.getVariable("studentEmail") as String
        val studentNotExists = !validationService.studentExists(studentEmail)

        if (studentNotExists) throw BpmnError("STUDENT_NOT_FOUND")
    }
}