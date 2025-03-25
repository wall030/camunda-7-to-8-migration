package com.wall.student_crm.enums

enum class MailMessageTemplate(val subject: String, val content: String) {
    EXAM_REJECTION_MESSAGE("Exam Rejection", "Exam registration rejected!"),
    EXAM_CONFIRMATION_MESSAGE("Exam Confirmation", "Exam registration confirmed!"),
    ALREADY_ENROLLED_MESSAGE("Exam registration process failed", "Student already enrolled in this course!"),
    STUDENT_NOT_FOUND_MESSAGE("Exam registration process failed", "Student not found. Student does not exist!"),
    COURSE_NOT_FOUND_MESSAGE("Exam registration process failed", "Course not found. Course does not exist!"),
    LATE_ENROLLMENT_MESSAGE("Exam registration process on hold", "There was an error enrolling your course/s. An Employee will enroll you manually")
}