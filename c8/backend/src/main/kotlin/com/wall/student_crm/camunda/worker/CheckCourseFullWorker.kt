package com.wall.student_crm.camunda.worker

import com.wall.student_crm.service.business.CourseService
import io.camunda.zeebe.client.ZeebeClient
import io.camunda.zeebe.client.api.response.ActivatedJob
import io.camunda.zeebe.spring.client.annotation.JobWorker
import org.springframework.stereotype.Component

@Component
class CheckCourseFullWorker(
    private val courseService: CourseService,
    private val zeebeClient: ZeebeClient
) {

    @JobWorker(type = "check-course-full")
    fun handle(job: ActivatedJob): Map<String, Any> {
        val variables = job.variablesAsMap
        val courseName = variables["course"] as String
        val courseIsFull = courseService.isCourseFull(courseName)

        if (courseIsFull) {
            zeebeClient.newPublishMessageCommand()
                .messageName("startReviseCourseSize")
                .withoutCorrelationKey()
                .variables(mapOf("course" to courseName))
                .send()
                .join()
        }
        return mapOf("courseIsFull" to courseIsFull)
    }
}