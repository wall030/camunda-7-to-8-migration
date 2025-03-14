package com.wall.student_crm.exception

sealed class ServiceException(message: String) : RuntimeException(message) {

    class StudentNotFoundException(studentIds: List<String>) :
        ServiceException("Student not found with IDs: ${studentIds.joinToString(", ")}") {
        constructor(studentId: String) : this(listOf(studentId))
    }

    class StudentEmailNotFoundException(email: String) :
        ServiceException("Student with email $email does not exist")

    class CourseNotFoundException(courseIds: List<Long>) :
        ServiceException("Course not found with IDs: ${courseIds.joinToString(", ")}") {
        constructor(courseId: Long) : this(listOf(courseId))
    }

    class CourseNameNotFoundException(name: String) :
        ServiceException("Course titled $name does not exist")

    class DuplicateStudentException(email: String) :
        ServiceException("Student with email $email already exists")

    class DuplicateCourseException(name: String) :
        ServiceException("Course titled $name already exists")
}