package com.wall.student_crm.camunda.delegates

import com.wall.student_crm.service.JustificationService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional("businessTransactionManager")
@Component
class RemoveJustificationDelegate(
    private val justificationService: JustificationService
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val justificationId = execution.getVariable("justificationId") as UUID
        justificationService.removeJustification(justificationId)
        execution.removeVariable("justification")
    }
}