package com.wall.student_crm.camunda.delegates.validation

import com.wall.student_crm.persistence.course.CourseRepository
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CheckCourseFullDelegate(
    private val courseRepository: CourseRepository,
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val courseName = execution.getVariable("course") as String
        val course = courseRepository.findByName(courseName)
        execution.setVariable("courseIsFull", course!!.currentSize >= course.maxSize)
    }
}