package com.wall.student_crm.service

import com.wall.student_crm.shared.enums.Status
import com.wall.student_crm.shared.enums.Status.*
import org.springframework.stereotype.Service


@Service
class StatusService {
    fun determineStatus(
        currentStatus: Status,
        registrationAllowed: Boolean,
        cancelRegistration: Boolean,
        acceptJustification: Boolean,
        overbooked: Boolean
    ): Status {
        return when (currentStatus) {
            NOT_INITIALIZED -> CHECKING
            CHECKING -> if (registrationAllowed) SUCCESSFUL else JUSTIFYING
            JUSTIFYING -> if (cancelRegistration) STOPPED else EXAM_OFFICE_CHECKING
            EXAM_OFFICE_CHECKING -> {
                when {
                    acceptJustification -> if (overbooked) SUCCESSFUL else REJECTED
                    else -> REJECTED
                }
            }
            else -> CHECKING
        }
    }
}