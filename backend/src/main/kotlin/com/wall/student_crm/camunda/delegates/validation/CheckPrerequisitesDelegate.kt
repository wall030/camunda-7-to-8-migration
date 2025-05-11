package com.wall.student_crm.camunda.delegates.validation

import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CheckPrerequisitesDelegate : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val prerequisite = execution.getVariable("prerequisite") as String
        val studentsPrerequisites = execution.getVariable("studentsPrerequisites") as List<*>

        if (!studentsPrerequisites.any { it == prerequisite }) {
            execution.setVariable("missingFound", true)
            throw BpmnError("PREREQUISITES_NOT_MET")
        }
    }
}