package com.wall.student_crm

import com.wall.student_crm.config.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
//@Deployment(resources = ["classpath:exam-registration.bpmn", "classpath:initial-existence-check.bpmn", "classpath:revise-course-size.bpmn", "classpath:checkExamRegistrationDeadline.dmn"])
class Application {

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}