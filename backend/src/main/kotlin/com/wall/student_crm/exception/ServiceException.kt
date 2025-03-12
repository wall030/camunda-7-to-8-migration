package com.wall.student_crm.exception

sealed class ServiceException(message: String) : RuntimeException(message) {

    class StudentNotFoundException(studentIds: List<Long>) :
        ServiceException(formatMessage("Student", studentIds)) {
        constructor(studentId: Long) : this(listOf(studentId))
    }

    class StudentEmailNotFoundException(email: String) :
        ServiceException("Student with email $email does not exist") {
    }

    class CourseNotFoundException(courseIds: List<Long>) :
        ServiceException(formatMessage("Course", courseIds)) {
        constructor(courseId: Long) : this(listOf(courseId))
    }

    class CourseNameNotFoundException(name: String) :
        ServiceException("Course titled $name does not exist")

    class DuplicateStudentException(email: String) :
        ServiceException("Student with email $email already exists")

    class DuplicateCourseException(name: String) :
        ServiceException("Course titled $name already exists")

    companion object {
        private fun formatMessage(entity: String, ids: List<Long>): String {
            return "${entity}${if (ids.size > 1) "s" else ""} not found with ID${if (ids.size > 1) "s" else ""}: ${
                ids.joinToString(", ")}"
        }
    }
}