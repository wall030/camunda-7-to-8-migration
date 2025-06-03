package com.wall.student_crm.camunda.worker

import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.shared.utils.TimeProvider
import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Component
class InitVariablesWorker(
    private val courseRepository: CourseRepository,
    private val timeProvider: TimeProvider
) {

    @JobWorker(type = "init-variables")
    @Transactional(readOnly = true)
    fun handle(job: ActivatedJob): Map<String, Any> {
        val variables = job.variablesAsMap
        val courseName = variables["course"] as String

        val prerequisitesList = courseRepository.findByName(courseName)?.prerequisites?.map { it.name } ?: emptyList()
        val studentsPrerequisites = mutableListOf<String>()

        val prerequisiteA = variables["prerequisiteA"] as Boolean
        val prerequisiteB = variables["prerequisiteB"] as Boolean
        val prerequisiteC = variables["prerequisiteC"] as Boolean
        val prerequisiteD = variables["prerequisiteD"] as Boolean

        if (prerequisiteA) studentsPrerequisites.add("prerequisite a")
        if (prerequisiteB) studentsPrerequisites.add("prerequisite b")
        if (prerequisiteC) studentsPrerequisites.add("prerequisite c")
        if (prerequisiteD) studentsPrerequisites.add("prerequisite d")

        val result = mutableMapOf<String,Any>(
            "prerequisitesList" to prerequisitesList,
            "studentsPrerequisites" to studentsPrerequisites
        )

        val currentMonth = timeProvider.now().format(DateTimeFormatter.ofPattern("MM"))
        if (variables["currentMonth"] == null) {
            result["currentMonth"] = currentMonth
        }

        return result
    }
}