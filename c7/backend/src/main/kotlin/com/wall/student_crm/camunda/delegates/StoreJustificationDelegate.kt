package com.wall.student_crm.camunda.delegates

import com.wall.student_crm.service.JustificationService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional("businessTransactionManager")
@Component
class StoreJustificationDelegate(
    private val justificationService: JustificationService
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val studentEmail = execution.getVariable("studentEmail") as String
        val justificationText = execution.getVariable("justification") as String
        val justificationId = justificationService.storeJustification(studentEmail, justificationText)
        execution.setVariable("justificationId", justificationId)
    }
}