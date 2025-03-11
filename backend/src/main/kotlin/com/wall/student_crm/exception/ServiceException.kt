package com.wall.student_crm.exception

sealed class ServiceException(message: String) : RuntimeException(message) {
    class StudentNotFoundException(studentId: String) :
        ServiceException("Student not found with ID: $studentId")

    class CourseNotFoundException(courseId: String) :
        ServiceException("Course not found with ID: $courseId")

    class DuplicateStudentException(email: String) :
        ServiceException("Student with email $email already exists")

    class DuplicateCourseException(name: String) :
        ServiceException("Course titled $name already exists")
}
