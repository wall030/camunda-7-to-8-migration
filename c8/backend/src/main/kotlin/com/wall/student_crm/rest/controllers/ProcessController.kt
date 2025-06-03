package com.wall.student_crm.rest.controllers

import com.wall.student_crm.rest.dto.process.RegistrationDto
import io.camunda.zeebe.client.ZeebeClient
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/process")
class ProcessController(
    private val zeebeClient: ZeebeClient
) {

    @PostMapping("/registerExam")
    @PreAuthorize("hasAuthority('ROLE_STUDENTS') or hasAuthority('ROLE_ADMIN')")
    fun startRegistrationProcess(@RequestBody registration: RegistrationDto): ResponseEntity<String> {
        val variables = mapOf(
            "studentEmail" to registration.studentEmail,
            "prerequisiteA" to registration.prerequisiteA,
            "prerequisiteB" to registration.prerequisiteB,
            "prerequisiteC" to registration.prerequisiteC,
            "prerequisiteD" to registration.prerequisiteD,
            "course" to registration.course
        )

        val processInstance = zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId("examRegistration")
            .latestVersion()
            .variables(variables)
            .send()
            .join()

        return ResponseEntity.ok(processInstance.processInstanceKey.toString())
    }
}