package com.wall.student_crm.delegates.validation

import com.wall.student_crm.persistence.course.CourseRepository
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CheckCourseExistsDelegate(
    private val courseRepository: CourseRepository
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val courseName = execution.getVariable("courseName").toString()
        courseRepository.findByName(courseName)
            ?: throw BpmnError("COURSE_NOT_FOUND")
        execution.setVariable("courseExists", true)
    }
}