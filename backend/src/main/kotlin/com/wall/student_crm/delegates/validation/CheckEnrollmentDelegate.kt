package com.wall.student_crm.delegates.validation

import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.student.StudentRepository
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CheckEnrollmentDelegate(
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val studentEmail = execution.getVariable("studentEmail").toString()
        val student = studentRepository.findByEmail(studentEmail)

        val courseName = execution.getVariable("course").toString()
        val course = courseRepository.findByName(courseName)

        if (student!!.courses.contains(course)) {
            execution.setVariable("error", true)
            throw BpmnError(
                "ALREADY_ENROLLED",
                "Student is already enrolled in this course."
            )
        }
        execution.setVariable("approved", true)
    }
}
