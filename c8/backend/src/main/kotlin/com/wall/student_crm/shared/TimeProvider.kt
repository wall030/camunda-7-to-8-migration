package com.wall.student_crm.shared

import java.time.LocalDate

interface TimeProvider {
    fun now(): LocalDate
}