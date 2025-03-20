package com.wall.student_crm.enums

enum class MailMessageTemplate(val subject: String, val content: String) {
    EXAM_REJECTION_MESSAGE("Exam Rejection", "Exam registration rejected!"),
    EXAM_CONFIRMATION_MESSAGE("Exam Confirmation", "Exam registration confirmed!"),
    ALREADY_ENROLLED_MESSAGE("Exam registration process failed", "Student already enrolled in this course!"),
    FAILED_CHECK_MESSAGE("Exam registration process failed", "Entity not found. Student or course does not exist!")
}