package com.wall.student_crm.unit

import com.wall.student_crm.camunda.delegates.EnrollCourseDelegate
import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.course.CourseEntity
import com.wall.student_crm.persistence.student.StudentRepository
import com.wall.student_crm.persistence.student.StudentEntity
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class EnrollCourseDelegateTest {
    @Mock
    private lateinit var studentRepository: StudentRepository

    @Mock
    private lateinit var courseRepository: CourseRepository

    @Mock
    private lateinit var delegateExecution: DelegateExecution

    @InjectMocks
    private lateinit var enrollCourseDelegate: EnrollCourseDelegate

    @Test
    fun `should enroll student in course successfully`() {
        val studentEmail = "test@student.com"
        val courseName = "Test Course"
        val student = StudentEntity(email = studentEmail, courses = mutableListOf())
        val course = CourseEntity(name = courseName)

        `when`(delegateExecution.getVariable("studentEmail")).thenReturn(studentEmail)
        `when`(delegateExecution.getVariable("course")).thenReturn(courseName)
        `when`(studentRepository.findByEmail(studentEmail)).thenReturn(student)
        `when`(courseRepository.findByName(courseName)).thenReturn(course)
        `when`(studentRepository.save(student)).thenReturn(student)

        enrollCourseDelegate.execute(delegateExecution)

        verify(studentRepository).findByEmail(studentEmail)
        verify(courseRepository).findByName(courseName)
        verify(studentRepository).save(student)
        Assertions.assertTrue(student.courses.contains(course))
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
        val student = StudentEntity(email = studentEmail, courses = mutableListOf())

        `when`(delegateExecution.getVariable("studentEmail")).thenReturn(studentEmail)
        `when`(delegateExecution.getVariable("course")).thenReturn(courseName)
        `when`(studentRepository.findByEmail(studentEmail)).thenReturn(student)
        `when`(courseRepository.findByName(courseName)).thenReturn(null)

        assertThrows<BpmnError> {
            enrollCourseDelegate.execute(delegateExecution)
        }.apply {
            Assertions.assertEquals("SYSTEM_ERROR", errorCode)
        }
    }
}