package com.wall.student_crm.service

import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.course.StudentCourseId
import com.wall.student_crm.persistence.course.StudentCourseRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ValidationService(
    private val camundaUserService: CamundaUserService,
    private val courseRepository: CourseRepository,
    private val studentCourseRepository: StudentCourseRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ValidationService::class.java)
    }

    @Transactional(readOnly = true)
    fun courseExists(courseName: String): Boolean {
        logger.info("Entering checkCourseExists with courseName={}", courseName)
        if (!courseRepository.existsByName(courseName)) {
            logger.warn("Course not found: {}", courseName)
            return false
        }
        logger.info("Course {} exists", courseName)
        return true
    }

    fun isCorrectEmailFormat(email: String): Boolean {
        logger.info("Entering checkEmailFormat")

        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!email.matches(regex)) {
            logger.warn("Invalid email format: {}", email)
            return false
        }
        logger.info("Email format is valid")
        return true
    }

    @Transactional(readOnly = true)
    fun isEnrolled(studentEmail: String, courseName: String): Boolean {
        logger.info("Entering checkEnrollment with courseName={}", courseName)
        val studentId = camundaUserService.getUserIdByEmail(studentEmail)!!
        val course = courseRepository.findByName(courseName)!!

        val enrollmentId = StudentCourseId(studentId, course.id)
        val isEnrolled = studentCourseRepository.existsById(enrollmentId)
        if (isEnrolled) {
            logger.warn("Student is already enrolled in course: {}", courseName)
        } else {
            logger.info("Student is not enrolled in course: {}", courseName)
        }
        return isEnrolled
    }

    fun isPrerequisiteMissing(prerequisite: String, studentsPrerequisites: List<String>): Boolean {
        logger.info("Entering isPrerequisiteMissing with prerequisite={}", prerequisite)
        val missing = prerequisite !in studentsPrerequisites
        logger.info("Prerequisite {} is {}", prerequisite, if (missing) "missing" else "met")
        return missing
    }

    @Transactional(readOnly = true)
    fun studentExists(studentEmail: String): Boolean {
        logger.info("Entering checkStudentExists")
        if (!camundaUserService.existsByEmail(studentEmail)) {
            logger.warn("Student not found for email: {}", studentEmail)
            return false
        }
        logger.info("Student exists")
        return true
    }
}