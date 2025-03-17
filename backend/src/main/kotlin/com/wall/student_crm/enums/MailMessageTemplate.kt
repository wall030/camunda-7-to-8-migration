package com.wall.student_crm.enums

enum class MailMessageTemplate(val subject: String, val content: String) {
    EXAM_REJECTION_MESSAGE("Exam Rejection", "Exam registration rejected!"),
    EXAM_CONFIRMATION_MESSAGE("Exam Confirmation", "Exam registration confirmed!")
}