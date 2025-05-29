package com.wall.student_crm.camunda.delegates

import com.wall.student_crm.persistence.CamundaUserService
import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.course.StudentCourseEntity
import com.wall.student_crm.persistence.course.StudentCourseRepository
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional("businessTransactionManager")
@Component
class EnrollCourseDelegate(
    private val camundaUserService: CamundaUserService,
    private val courseRepository: CourseRepository,
    private val studentCourseRepository: StudentCourseRepository
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        try {
            val studentEmail = execution.getVariable("studentEmail") as String
            val courseName = execution.getVariable("course") as String
            val studentId = camundaUserService.getUserIdByEmail(studentEmail)!!
            val course = courseRepository.findByName(courseName)!!
            val enrollment = StudentCourseEntity(studentId = studentId, courseId = course.id)
            studentCourseRepository.save(enrollment)

            course.currentSize++
            courseRepository.save(course)

        } catch (e: Exception) {
            throw BpmnError("SYSTEM_ERROR")
        }
    }
}

