package com.wall.student_crm.camunda.delegates

import com.wall.student_crm.persistence.justification.JustificationRepository
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional("businessTransactionManager")
@Component
class RemoveJustificationDelegate(
    private val justificationRepository: JustificationRepository
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val id = execution.getVariable("justificationId") as UUID
        val justification = justificationRepository.findById(id).get()
        justificationRepository.delete(justification)
        execution.removeVariable("justification")
    }
}