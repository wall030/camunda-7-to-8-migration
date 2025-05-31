package com.wall.student_crm.camunda.delegates

import com.wall.student_crm.service.CourseService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional("businessTransactionManager")
@Component
class EnrollCourseDelegate(
    private val courseService: CourseService
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val studentEmail = execution.getVariable("studentEmail") as String
        val courseName = execution.getVariable("course") as String
        courseService.enrollStudent(studentEmail, courseName)
    }
}

