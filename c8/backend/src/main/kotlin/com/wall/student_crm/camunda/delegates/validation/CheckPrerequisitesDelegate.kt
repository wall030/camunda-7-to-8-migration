package com.wall.student_crm.camunda.delegates.validation

import com.wall.student_crm.service.ValidationService
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CheckPrerequisitesDelegate(
    private val validationService: ValidationService
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val prerequisite = execution.getVariable("prerequisite") as String
        val studentsPrerequisites = execution.getVariable("studentsPrerequisites") as List<String>

        if (validationService.isPrerequisiteMissing(prerequisite, studentsPrerequisites)) {
            execution.setVariable("missingFound", true)
            throw BpmnError("PREREQUISITES_NOT_MET")
        }
    }
}