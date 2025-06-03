package com.wall.student_crm.camunda.worker

import com.wall.student_crm.service.business.CourseService
import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.springframework.stereotype.Component

@Component
class IncreaseCourseSizeWorker(
    private val courseService: CourseService
) {

    @JobWorker(type = "increase-course-size")
    fun handle(job: ActivatedJob) {
        val variables = job.variablesAsMap
        val courseName = variables["course"] as String
        courseService.increaseCourseSize(courseName)
    }
}