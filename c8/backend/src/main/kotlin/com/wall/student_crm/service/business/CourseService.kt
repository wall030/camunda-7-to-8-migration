package com.wall.student_crm.service.business

import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.course.StudentCourseEntity
import com.wall.student_crm.persistence.course.StudentCourseRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourseService(
    private val userService: UserService,
    private val courseRepository: CourseRepository,
    private val studentCourseRepository: StudentCourseRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(CourseService::class.java)
    }

    @Transactional
    fun enrollStudent(studentEmail: String, courseName: String) {
        logger.info("Entering enrollStudent with studentEmail={}, courseName={}", studentEmail, courseName)

        val studentId = userService.findByEmail(studentEmail)!!.id

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
    }

    @Transactional
    fun checkCourseFullWithReservation(courseName: String): Boolean {
        logger.info("Entering isCourseFull with courseName={}", courseName)
        val course = courseRepository.findByName(courseName)!!
        val isFull = course.currentSize >= course.maxSize
        if (isFull) {
            logger.info("Course {} is full: {}", courseName, isFull)
            return true
        }

        val oldSize = course.currentSize
        course.currentSize++
        courseRepository.save(course)
        logger.info("Increased course {} currentSize to {}", courseName, course.currentSize)

        logger.info(
            "Course {} spot RESERVED: {}/{} -> {}/{}",
            courseName, oldSize, course.maxSize, course.currentSize, course.maxSize
        )

        return false
    }

    @Transactional
    fun increaseCourseSize(courseName: String) {
        logger.info("Entering increaseCourseSize with courseName={}", courseName)
        val course = courseRepository.findByName(courseName)!!
        course.maxSize++
        courseRepository.save(course)
        logger.info("Increased course {} maxSize to {}", courseName, course.maxSize)
    }

    // free up spot when enrollment fails (compensation)
    @Transactional
    fun releaseReservedSpot(courseName: String) {
        logger.info("Releasing reserved spot for course {}", courseName)

        try {
            val course = courseRepository.findByName(courseName)!!

            if (course.currentSize > 0) {
                course.currentSize--
                courseRepository.save(course)
                logger.info(
                    "Released spot for course {} - New size: {}/{}",
                    courseName, course.currentSize, course.maxSize
                )
            } else {
                logger.warn("Cannot release spot for course {} - currentSize is already 0", courseName)
            }

        } catch (e: Exception) {
            logger.error("Failed to release spot for course {}", courseName, e)
        }
    }
}