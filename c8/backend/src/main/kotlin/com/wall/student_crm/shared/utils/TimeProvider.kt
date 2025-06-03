package com.wall.student_crm.shared.utils

import java.time.LocalDate

interface TimeProvider {
    fun now(): LocalDate
}