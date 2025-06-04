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

    // Cache for sent Messages (idempotence)
    private val sentMessages = mutableSetOf<String>()

    @JobWorker(type = "check-course-full")
    fun handle(job: ActivatedJob): Map<String, Any> {
        val variables = job.variablesAsMap
        val courseName = variables["course"] as String
        val courseIsFull = courseService.checkCourseFullWithReservation(courseName)

        if (courseIsFull) {
            val messageKey = "revise-course-size:$courseName"

            if (!sentMessages.contains(messageKey)) {
                zeebeClient.newPublishMessageCommand()
                    .messageName("startReviseCourseSize")
                    .withoutCorrelationKey()
                    .variables(mapOf("course" to courseName))
                    .send()
                    .join()

                sentMessages.add(messageKey)
            }
        }
        return mapOf("courseIsFull" to courseIsFull)
    }
}