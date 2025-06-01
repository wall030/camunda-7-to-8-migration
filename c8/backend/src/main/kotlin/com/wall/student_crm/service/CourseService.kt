package com.wall.student_crm.service

import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.course.StudentCourseEntity
import com.wall.student_crm.persistence.course.StudentCourseRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourseService(
    private val camundaUserService: CamundaUserService,
    private val courseRepository: CourseRepository,
    private val studentCourseRepository: StudentCourseRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(CourseService::class.java)
    }

    @Transactional
    fun enrollStudent(studentEmail: String, courseName: String) {
        logger.info("Entering enrollStudent with studentEmail={}, courseName={}", studentEmail, courseName)

        val studentId = camundaUserService.getUserIdByEmail(studentEmail)!!

        // Get course with optimistic locking
        val course = courseRepository.findByName(courseName)!!

        // Check for existing enrollment (idempotence)
        if (studentCourseRepository.existsByStudentIdAndCourseId(studentId, course.id)) {
            logger.info("Student {} already enrolled in course {}", studentId, courseName)
            return
        }

        val enrollment = StudentCourseEntity(studentId = studentId, courseId = course.id)
        studentCourseRepository.save(enrollment)
        logger.info("Enrolled student {} in course {}", studentId, courseName)

        course.currentSize++
        courseRepository.save(course)
        logger.info("Increased course {} currentSize to {}", courseName, course.currentSize)
    }

    @Transactional(readOnly = true)
    fun isCourseFull(courseName: String): Boolean {
        logger.info("Entering isCourseFull with courseName={}", courseName)
        val course = courseRepository.findByName(courseName)!!
        val isFull = course.currentSize >= course.maxSize
        logger.info("Course {} is full: {}", courseName, isFull)
        return isFull
    }

    @Transactional
    fun increaseCourseSize(courseName: String) {
        logger.info("Entering increaseCourseSize with courseName={}", courseName)
        val course = courseRepository.findByName(courseName)!!
        course.maxSize++
        courseRepository.save(course)
        logger.info("Increased course {} maxSize to {}", courseName, course.maxSize)
    }
}