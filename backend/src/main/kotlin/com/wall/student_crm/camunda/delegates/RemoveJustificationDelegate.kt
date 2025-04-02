package com.wall.student_crm.camunda.delegates

import com.wall.student_crm.persistence.justification.JustificationRepository
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RemoveJustificationDelegate(
    private val justificationRepository: JustificationRepository
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val id = execution.getVariable("justificationId") as String
        justificationRepository.findById(UUID.fromString(id)).ifPresent { justificationRepository.delete(it) }
        execution.removeVariable("justification")
    }
}