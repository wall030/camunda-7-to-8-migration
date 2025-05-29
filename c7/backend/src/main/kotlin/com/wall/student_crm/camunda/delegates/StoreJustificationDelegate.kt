package com.wall.student_crm.camunda.delegates

import com.wall.student_crm.persistence.CamundaUserService
import com.wall.student_crm.persistence.justification.JustificationEntity
import com.wall.student_crm.persistence.justification.JustificationRepository
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional("businessTransactionManager")
@Component
class StoreJustificationDelegate(
    private val justificationRepository: JustificationRepository,
    private val camundaUserService: CamundaUserService
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        try {
            val email = execution.getVariable("studentEmail") as String
            val justificationText = execution.getVariable("justification") as String

            val studentId = camundaUserService.getUserIdByEmail(email)!!

            val justification = JustificationEntity(
                studentId = studentId,
                justification = justificationText
            )
            val saved = justificationRepository.save(justification)
            execution.setVariable("justificationId", saved.id)
        } catch (e: Exception) {
            throw BpmnError("SYSTEM_ERROR")
        }
    }
}