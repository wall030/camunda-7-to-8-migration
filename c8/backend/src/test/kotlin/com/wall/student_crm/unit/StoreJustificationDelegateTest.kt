package com.wall.student_crm.unit

import com.wall.student_crm.camunda.worker.StoreJustificationDelegate
import com.wall.student_crm.service.CamundaUserService
import com.wall.student_crm.persistence.justification.JustificationEntity
import com.wall.student_crm.persistence.justification.JustificationRepository
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.eq
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class StoreJustificationDelegateTest {

    @Mock
    private lateinit var justificationRepository: JustificationRepository

    @Mock
    private lateinit var camundaUserService: CamundaUserService

    @Mock
    private lateinit var delegateExecution: DelegateExecution

    @InjectMocks
    private lateinit var storeJustificationDelegate: StoreJustificationDelegate

    @Test
    fun `should store justification successfully`() {
        val studentEmail = "test@student.com"
        val justificationText = "Missed due to illness"
        val studentId = "id"

        val justification = JustificationEntity(studentId = studentId, justification = justificationText)

        `when`(delegateExecution.getVariable("studentEmail")).thenReturn(studentEmail)
        `when`(delegateExecution.getVariable("justification")).thenReturn(justificationText)
        `when`(camundaUserService.getUserIdByEmail(studentEmail)).thenReturn(studentId)
        `when`(justificationRepository.save(any<JustificationEntity>())).thenReturn(justification)

        storeJustificationDelegate.execute(delegateExecution)

        val justificationIdCaptor = ArgumentCaptor.forClass(UUID::class.java)
        verify(delegateExecution).setVariable(eq("justificationId"), justificationIdCaptor.capture())


        verify(justificationRepository).save(any<JustificationEntity>())
        assertEquals(studentId, justification.studentId)
        assertEquals(justificationText, justification.justification)
    }
}
