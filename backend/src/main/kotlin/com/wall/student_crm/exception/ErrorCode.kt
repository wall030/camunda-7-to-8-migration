package com.wall.student_crm.exception

enum class ErrorCode(val code: Int, val message: String) {
    STUDENT_NOT_FOUND(1001, "Student not found"),
    COURSE_NOT_FOUND(1002, "Course not found"),
    DUPLICATE_STUDENT(1003, "Student already exists with the provided email"),
    DUPLICATE_COURSE(1004, "Course with the specified title already exists"),
    INTERNAL_ERROR(5000, "An unexpected internal error occurred"),
    ;

    companion object {
        fun fromException(exception: ServiceException): ErrorCode =
            when (exception) {
                is ServiceException.StudentNotFoundException -> STUDENT_NOT_FOUND
                is ServiceException.StudentEmailNotFoundException -> STUDENT_NOT_FOUND
                is ServiceException.CourseNameNotFoundException -> COURSE_NOT_FOUND
                is ServiceException.CourseNotFoundException -> COURSE_NOT_FOUND
                is ServiceException.DuplicateStudentException -> DUPLICATE_STUDENT
                is ServiceException.DuplicateCourseException -> DUPLICATE_COURSE
            }
    }
}
