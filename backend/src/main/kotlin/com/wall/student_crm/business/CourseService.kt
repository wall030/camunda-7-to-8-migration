package com.wall.student_crm.business

import com.wall.student_crm.exception.ServiceException
import com.wall.student_crm.http.course.CourseDTO
import com.wall.student_crm.http.student.StudentDTO
import com.wall.student_crm.persistence.course.CourseEntity
import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.student.StudentRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val studentRepository: StudentRepository,
) {
    @Transactional
    fun createCourse(name: String): CourseDTO {
        courseRepository.findByName(name)?.let {
            throw ServiceException.DuplicateCourseException(name)
        }
        val createdCourse = CourseEntity(name)
        courseRepository.save(createdCourse)
        return createdCourse.toCourseDTO()
    }

    @Transactional
    fun deleteCourses(courseIDs: List<Long>): Boolean {
        if (courseIDs.isNotEmpty()) {
            val courses = courseRepository.findAllById(courseIDs)
            val fetchedCourseIds = courses.map { it.id }
            val missingCourses = courseIDs - fetchedCourseIds.toSet()
            if (missingCourses.isNotEmpty()) throw ServiceException.CourseNotFoundException(missingCourses)
            courseRepository.deleteAllById(courseIDs)
            return true
        }
        return false
    }

    @Transactional
    fun assignStudents(
        id: Long,
        students: List<String>,
    ): List<StudentDTO> {
        val course =
            courseRepository.findById(id)
                .orElseThrow { ServiceException.CourseNotFoundException(id) }

        val fetchedStudents = studentRepository.findAllById(students)
        val fetchedStudentIds = fetchedStudents.map { it.id }
        val missingStudentsList = students - fetchedStudentIds.toSet()
        if (missingStudentsList.isNotEmpty()) throw ServiceException.StudentNotFoundException(missingStudentsList)
        course.students.forEach { student ->
            if (!students.contains(student.id)) {
                student.courses.remove(course)
            }
        }
        course.students.clear()
        course.students.addAll(fetchedStudents)
        fetchedStudents.forEach { student ->
            if (!student.courses.contains(course)) {
                student.courses.add(course)
            }
        }
        return course.students.map { StudentDTO(it.id, it.firstName, it.lastName, it.email, emptyList()) }
    }
}