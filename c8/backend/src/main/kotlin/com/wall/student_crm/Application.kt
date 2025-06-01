package com.wall.student_crm

import io.camunda.zeebe.spring.client.annotation.Deployment
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
//@Deployment(resources = ["classpath:exam-registration.bpmn", "classpath:initial-existence-check.bpmn", "classpath:revise-course-size.bpmn", "classpath:checkExamRegistrationDeadline.dmn"])
class Application {

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}