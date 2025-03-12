package com.wall.student_crm.http

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.camunda.bpm.engine.RuntimeService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/process")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProcessController(
    private val runtimeService: RuntimeService
) {

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/start/{processKey}")
    fun startProcess(
        @PathVariable processKey: String,
        @RequestBody variables: Map<String, Any>
    ) = runtimeService.startProcessInstanceById(processKey, variables)
}
