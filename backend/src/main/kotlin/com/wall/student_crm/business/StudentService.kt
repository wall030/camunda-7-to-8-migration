package com.wall.student_crm.business

import com.wall.student_crm.exception.ServiceException
import com.wall.student_crm.http.course.CourseDTO
import com.wall.student_crm.http.student.StudentDTO
import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.student.StudentEntity
import com.wall.student_crm.persistence.student.StudentRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class StudentService (
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
    ) {

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
            if (missingStudents.isNotEmpty()) throw ServiceException.StudentNotFoundException(missingStudents.toString())
            studentRepository.deleteAllById(studentIDs)
            return true
        }

        @Transactional
        fun assignCourses(
            id: Long,
            courses: List<Long>,
        ): List<CourseDTO> {
            val student = studentRepository.findById(id)
                .orElseThrow { ServiceException.StudentNotFoundException(id.toString()) }
            val fetchedCourses = courseRepository.findAllById(courses)
            val fetchedCourseIDs = fetchedCourses.map { it.id }
            val missingCoursesList = courses - fetchedCourseIDs.toSet()
            if (missingCoursesList.isNotEmpty()) throw ServiceException.CourseNotFoundException(missingCoursesList.toString())
            student.courses = fetchedCourses.toMutableList()
            studentRepository.save(student)

            return student.courses.map { CourseDTO(it.id, it.name, emptyList()) }
        }
    }