package com.wall.student_crm.unit

import com.wall.student_crm.camunda.delegates.EnrollCourseDelegate
import com.wall.student_crm.service.CamundaUserService
import com.wall.student_crm.persistence.course.CourseEntity
import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.course.StudentCourseEntity
import com.wall.student_crm.persistence.course.StudentCourseRepository
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class EnrollCourseDelegateTest {
    @Mock
    private lateinit var camundaUserService: CamundaUserService

    @Mock
    private lateinit var courseRepository: CourseRepository

    @Mock
    private lateinit var studentCourseRepository: StudentCourseRepository

    @Mock
    private lateinit var delegateExecution: DelegateExecution

    @InjectMocks
    private lateinit var enrollCourseDelegate: EnrollCourseDelegate

    @Test
    fun `should enroll student in course successfully`() {
        val studentEmail = "test@student.com"
        val courseName = "Test Course"
        val userId = "id"
        val course = CourseEntity(id = 1L, name = courseName, currentSize = 0)
        val enrollment = StudentCourseEntity(userId, course.id)

        `when`(delegateExecution.getVariable("studentEmail")).thenReturn(studentEmail)
        `when`(delegateExecution.getVariable("course")).thenReturn(courseName)
        `when`(camundaUserService.getUserIdByEmail(studentEmail)).thenReturn(userId)
        `when`(courseRepository.findByName(courseName)).thenReturn(course)
        `when`(studentCourseRepository.save(any())).thenReturn(enrollment)
        `when`(courseRepository.save(course)).thenReturn(course)

        enrollCourseDelegate.execute(delegateExecution)

        verify(camundaUserService).getUserIdByEmail(studentEmail)
        verify(courseRepository).findByName(courseName)
        verify(courseRepository).save(course)
        verify(studentCourseRepository).save(any())
        assert(course.currentSize == 1)

    }

    @Test
    fun `should throw BpmnError when student is not found`() {
        `when`(delegateExecution.getVariable("studentEmail")).thenReturn("notfound@student.com")

        assertThrows<BpmnError> {
            enrollCourseDelegate.execute(delegateExecution)
        }.apply {
            Assertions.assertEquals("SYSTEM_ERROR", errorCode)
        }
    }

    @Test
    fun `should throw BpmnError when course is not found`() {
        val studentEmail = "test@student.com"
        val courseName = "Test Course"
        val studentId = "id"

        `when`(delegateExecution.getVariable("studentEmail")).thenReturn(studentEmail)
        `when`(delegateExecution.getVariable("course")).thenReturn(courseName)
        `when`(camundaUserService.getUserIdByEmail(studentEmail)).thenReturn(studentId)
        `when`(courseRepository.findByName(courseName)).thenReturn(null)

        assertThrows<BpmnError> {
            enrollCourseDelegate.execute(delegateExecution)
        }.apply {
            Assertions.assertEquals("SYSTEM_ERROR", errorCode)
        }
    }
}