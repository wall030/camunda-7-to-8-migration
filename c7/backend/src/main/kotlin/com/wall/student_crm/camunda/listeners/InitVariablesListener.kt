package com.wall.student_crm.camunda.listeners

import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.shared.TimeProvider
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.ExecutionListener
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class InitVariablesListener(
    private val courseRepository: CourseRepository,
    private val timeProvider: TimeProvider
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

        execution.setVariable("prerequisitesList", prerequisitesList)
        execution.setVariable("studentsPrerequisites", studentsPrerequisites)
        execution.setVariable("missingFound", false)

        val currentMonth = timeProvider.now().format(DateTimeFormatter.ofPattern("MM"))
        if (execution.getVariable("currentMonth") == null) {
            execution.setVariable("currentMonth", currentMonth)
        }
    }
}