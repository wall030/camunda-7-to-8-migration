package com.wall.student_crm.delegates

import com.wall.student_crm.exception.ServiceException
import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.student.StudentRepository
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CheckExistenceDelegate(
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val studentEmail = execution.getVariable("studentEmail").toString()
        val courseName = execution.getVariable("courseName").toString()
        val student = studentRepository.findByEmail(studentEmail)
            ?: throw ServiceException.StudentEmailNotFoundException(studentEmail)
        val course = courseRepository.findByName(courseName)
            ?: throw ServiceException.CourseNameNotFoundException(courseName)

        if (!student.courses.contains(course)) {
            execution.setVariable("approved", true)
        } else {
            execution.setVariable("approved", false)
        }
    }

}