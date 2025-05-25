package com.wall.student_crm.camunda.delegates.validation

import com.wall.student_crm.persistence.CamundaUserService
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CheckStudentExistsDelegate(
    private val camundaUserService: CamundaUserService
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val studentEmail = execution.getVariable("studentEmail") as String
        val studentId = camundaUserService.getUserIdByEmail(studentEmail)
        if (studentId == null) {
            throw BpmnError("STUDENT_NOT_FOUND")
        }
        execution.setVariable("studentExists", true)
    }
}