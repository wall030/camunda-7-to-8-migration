package com.wall.student_crm.camunda.delegates

import com.wall.student_crm.persistence.course.CourseEntity
import com.wall.student_crm.persistence.course.CourseRepository
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional("businessTransactionManager")
@Component
class IncreaseCourseSizeDelegate (
    private val courseRepository: CourseRepository,
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val courseName = execution.getVariable("course") as String
        var course = courseRepository.findByName(courseName) as CourseEntity
        course.maxSize += 1
        courseRepository.save<CourseEntity>(course)
    }
}