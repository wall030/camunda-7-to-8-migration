package com.wall.student_crm.camunda.delegates.validation

import com.wall.student_crm.service.ValidationService
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CheckCourseExistsDelegate(
    private val validationService: ValidationService
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val courseName = execution.getVariable("course") as String
        val courseNotExists = !validationService.courseExists(courseName)

        if (courseNotExists) throw BpmnError("COURSE_NOT_FOUND")
    }
}