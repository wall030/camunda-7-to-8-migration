package com.wall.student_crm.http.course

import com.wall.student_crm.business.CourseService
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/course")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CourseResource(
    var courseService: CourseService,
) {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    fun createCourse(
        @Valid courseDTO: CourseCreateUpdateDTO,
    ) = courseService.createCourse(courseDTO.name)

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/delete")
    fun deleteCourses(courses: List<Long>) = courseService.deleteCourses(courses)

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/assignStudents")
    fun assignStudents(
        @PathParam("id") id: Long,
        students: List<String>,
    ) = courseService.assignStudents(id, students)
}