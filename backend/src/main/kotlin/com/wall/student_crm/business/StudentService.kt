package com.wall.student_crm.business

import com.wall.student_crm.exception.ServiceException
import com.wall.student_crm.http.student.StudentDTO
import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.student.StudentEntity
import com.wall.student_crm.persistence.student.StudentRepository
import jakarta.transaction.Transactional
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.stereotype.Service

@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
) {

    fun initialCheckDelegate(
        courseName: String,
        email: String,
        execution: DelegateExecution
    ) {
        val student = studentRepository.findByEmail(email)
            ?: throw ServiceException.StudentEmailNotFoundException(email)
        val course = courseRepository.findByName(courseName)
            ?: throw ServiceException.CourseNameNotFoundException(courseName)

        if( !student.courses.contains(course) ) {
            execution.setVariable("approved", true)
        } else {
            execution.setVariable("approved", false)
        }
    }

    @Transactional
    fun assignCourseDelegate(
        email: String,
        courseName: String,
    ) {
        val student = studentRepository.findByEmail(email)!!
        val fetchedCourse = courseRepository.findByName(courseName)
        student.courses.add(fetchedCourse!!)
        studentRepository.save(student)
    }

    @Transactional
    fun createStudent(
        firstName: String,
        lastName: String,
        email: String,
    ): StudentDTO {
        studentRepository.findByEmail(email)?.let {
            throw ServiceException.DuplicateStudentException(email)
        }
        val createdStudent = StudentEntity(firstName, lastName, email)
        studentRepository.save(createdStudent)
        return createdStudent.toStudentDTO()
    }

    @Transactional
    fun deleteStudents(studentIDs: List<Long>): Boolean {
        if (studentIDs.isEmpty()) return false
        val students = studentRepository.findAllById(studentIDs)
        val fetchedStudentsIDs = students.map { it.id }
        val missingStudents = studentIDs - fetchedStudentsIDs.toSet()
        if (missingStudents.isNotEmpty()) throw ServiceException.StudentNotFoundException(missingStudents)
        studentRepository.deleteAllById(studentIDs)
        return true
    }
}