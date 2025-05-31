package com.wall.student_crm.camunda.delegates

import com.wall.student_crm.service.CourseService
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CheckCourseFullDelegate(
    private val courseService: CourseService,
    private val runtimeService: RuntimeService
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val courseName = execution.getVariable("course") as String
        val courseIsFull = courseService.isCourseFull(courseName)
        execution.setVariable("courseIsFull", courseIsFull)

        if (courseIsFull) {
            runtimeService.startProcessInstanceByMessage("startReviseCourseSize", mapOf("course" to courseName))
        }
    }
}