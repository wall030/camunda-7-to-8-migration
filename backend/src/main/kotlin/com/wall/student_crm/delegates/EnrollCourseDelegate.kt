package com.wall.student_crm.delegates

import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.student.StudentRepository
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class EnrollCourseDelegate(
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        try {
            val studentEmail = execution.getVariable("studentEmail").toString()
            val courseName = execution.getVariable("course").toString()
            val student = studentRepository.findByEmail(studentEmail)!!
            val fetchedCourse = courseRepository.findByName(courseName)
            student.courses.add(fetchedCourse!!)
            studentRepository.save(student)
        } catch (e: Exception) {
            throw BpmnError("SYSTEM_ERROR", "Unexpected error while assigning course.")
        }
    }
}

