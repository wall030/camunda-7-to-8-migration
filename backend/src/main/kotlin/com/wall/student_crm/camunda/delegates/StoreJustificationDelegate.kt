package com.wall.student_crm.camunda.delegates

import com.wall.student_crm.persistence.justification.JustificationEntity
import com.wall.student_crm.persistence.justification.JustificationRepository
import com.wall.student_crm.persistence.student.StudentRepository
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class StoreJustificationDelegate(
    private val justificationRepository: JustificationRepository,
    private val studentRepository: StudentRepository
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val email = execution.getVariable("studentEmail") as String
        val justificationText = execution.getVariable("justification") as String
        val studentId = studentRepository.findByEmail(email)!!.id
        val justification = JustificationEntity(studentId = studentId, justification = justificationText)
        execution.setVariable("justificationId", justification.id.toString())
        justificationRepository.save(justification)

    }
}