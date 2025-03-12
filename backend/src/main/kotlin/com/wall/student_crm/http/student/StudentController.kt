package com.wall.student_crm.http.student

import com.wall.student_crm.business.StudentService
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import org.springframework.http.HttpStatus
import jakarta.ws.rs.core.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/student")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StudentController(
    var studentService: StudentService,
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    fun createStudent(
        @Valid student: StudentCreateUpdateDTO,
    ) = studentService.createStudent(student.firstName, student.lastName, student.email)

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/delete")
    fun deleteStudents(studentIDs: List<Long>) = studentService.deleteStudents(studentIDs)
}
