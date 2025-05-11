package com.wall.student_crm.camunda.listeners

import com.wall.student_crm.persistence.course.CourseRepository
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.ExecutionListener
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class InitVariablesListener(
    private val courseRepository: CourseRepository
) : ExecutionListener {
    override fun notify(execution: DelegateExecution) {
        val courseName = execution.getVariable("course") as String

        val prerequisitesList = courseRepository.findByName(courseName)?.prerequisites?.map { it.name } ?: emptyList()
        val studentsPrerequisites = mutableListOf<String>()

        val prerequisiteA = execution.getVariable("prerequisiteA") as Boolean
        val prerequisiteB = execution.getVariable("prerequisiteB") as Boolean
        val prerequisiteC = execution.getVariable("prerequisiteC") as Boolean
        val prerequisiteD = execution.getVariable("prerequisiteD") as Boolean

        if (prerequisiteA) {
            studentsPrerequisites.add("prerequisite a")
        }
        if (prerequisiteB) {
            studentsPrerequisites.add("prerequisite b")
        }
        if (prerequisiteC) {
            studentsPrerequisites.add("prerequisite c")
        }
        if (prerequisiteD) {
            studentsPrerequisites.add("prerequisite d")
        }

        val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"))

        execution.setVariable("prerequisitesList", prerequisitesList)
        execution.setVariable("studentsPrerequisites", studentsPrerequisites)
        execution.setVariable("currentMonth", currentMonth)
        execution.setVariable("missingFound", false)
    }
}