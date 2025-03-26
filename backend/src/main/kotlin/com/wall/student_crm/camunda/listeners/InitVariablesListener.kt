package com.wall.student_crm.camunda.listeners

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.ExecutionListener
import org.springframework.stereotype.Component

@Component
class InitVariablesListener : ExecutionListener {
    override fun notify(execution: DelegateExecution) {
        val selectedCourses: MutableList<String> = mutableListOf()

        val courseA = execution.getVariable("courseA") as Boolean
        val courseB = execution.getVariable("courseB") as Boolean
        val courseC = execution.getVariable("courseC") as Boolean

        if (courseA) selectedCourses.add("Course A")
        if (courseB) selectedCourses.add("Course B")
        if (courseC) selectedCourses.add("Course C")

        execution.setVariable("selectedCoursesList", selectedCourses)
    }
}