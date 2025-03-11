package com.wall.student_crm.http

import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import org.springframework.http.HttpStatus
import jakarta.ws.rs.core.MediaType
import org.springframework.web.bind.annotation.ResponseStatus


@Path("/api/student")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StudentController(
    var studentService: StudentService,
) {

    @POST
    @ResponseStatus(HttpStatus.CREATED)
    @Path("/create")
    fun createStudent(
        @Valid student: StudentCreateUpdateDTO,
    ) = studentService.createStudent(student.firstName, student.lastName, student.email)

    @DELETE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Path("/delete")
    fun deleteStudents(studentIDs: List<Long>) = studentService.deleteStudents(studentIDs)

    @PUT
    @ResponseStatus(HttpStatus.OK)
    @Path("/{id}/assignCourses")
    fun assignCourses(
        @PathParam("id") id: Long,
        courses: List<Long>,
    ) = studentService.assignCourses(id, courses)
}
