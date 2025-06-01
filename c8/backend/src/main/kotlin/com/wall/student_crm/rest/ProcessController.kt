package com.wall.student_crm.rest

import com.wall.student_crm.rest.dto.RegistrationDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/process")
class ProcessController(
    // private val runtimeService: RuntimeService
) {
/*
    @PostMapping("/registerExam")
    fun startRegistrationProcess(@RequestBody registration: RegistrationDto): ResponseEntity<String> {
        val variables = mapOf(
            "studentEmail" to registration.studentEmail,
            "prerequisiteA" to registration.prerequisiteA,
            "prerequisiteB" to registration.prerequisiteB,
            "prerequisiteC" to registration.prerequisiteC,
            "prerequisiteD" to registration.prerequisiteD,
            "course" to registration.course
        )

        val processInstance = runtimeService.startProcessInstanceByKey("examRegistration", variables)
        return ResponseEntity.ok(processInstance.id)
    }
 */
}