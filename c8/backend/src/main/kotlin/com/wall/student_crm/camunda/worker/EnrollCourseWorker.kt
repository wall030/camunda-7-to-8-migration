package com.wall.student_crm.camunda.worker

import com.wall.student_crm.service.CourseService
import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.springframework.stereotype.Component

@Component
class EnrollCourseWorker(
    private val courseService: CourseService
) {

    @JobWorker(type = "enroll-course")
    fun handle(job: ActivatedJob) {
        val variables = job.variablesAsMap
        val studentEmail = variables["studentEmail"] as String
        val courseName = variables["course"] as String
        courseService.enrollStudent(studentEmail, courseName)
    }
}