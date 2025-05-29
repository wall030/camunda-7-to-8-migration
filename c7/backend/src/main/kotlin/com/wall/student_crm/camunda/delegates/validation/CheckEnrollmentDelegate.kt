package com.wall.student_crm.camunda.delegates.validation

import com.wall.student_crm.persistence.CamundaUserService
import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.course.StudentCourseId
import com.wall.student_crm.persistence.course.StudentCourseRepository
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CheckEnrollmentDelegate(
    private val camundaUserService: CamundaUserService,
    private val courseRepository: CourseRepository,
    private val studentCourseRepository: StudentCourseRepository
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val studentEmail = execution.getVariable("studentEmail") as String
        val courseName = execution.getVariable("course") as String

        val studentId = camundaUserService.getUserIdByEmail(studentEmail)!!
        val course = courseRepository.findByName(courseName)!!

        val enrollmentId = StudentCourseId(studentId, course.id)
        if (studentCourseRepository.existsById(enrollmentId)) {
            throw BpmnError("ALREADY_ENROLLED")
        }
    }
}