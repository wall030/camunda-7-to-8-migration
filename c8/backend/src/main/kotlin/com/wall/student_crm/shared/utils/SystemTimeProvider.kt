package com.wall.student_crm.shared.utils

import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class SystemTimeProvider : TimeProvider {
    override fun now(): LocalDate = LocalDate.now()
}
