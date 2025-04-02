package com.wall.student_crm

import com.wall.student_crm.camunda.delegates.EnrollCourseDelegate
import com.wall.student_crm.camunda.delegates.RemoveJustificationDelegate
import com.wall.student_crm.camunda.delegates.StoreJustificationDelegate
import com.wall.student_crm.camunda.delegates.validation.CheckCourseExistsDelegate
import com.wall.student_crm.camunda.delegates.validation.CheckEmailFormatDelegate
import com.wall.student_crm.camunda.delegates.validation.CheckEnrollmentDelegate
import com.wall.student_crm.camunda.delegates.validation.CheckStudentExistsDelegate
import com.wall.student_crm.camunda.listeners.InitVariablesListener
import com.wall.student_crm.camunda.listeners.StatusListener
import com.wall.student_crm.persistence.course.CourseEntity
import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.justification.JustificationRepository
import com.wall.student_crm.persistence.student.StudentEntity
import com.wall.student_crm.persistence.student.StudentRepository
import com.wall.student_crm.shared.mail.MailService
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables
import org.camunda.bpm.engine.test.mock.Mocks
import org.camunda.bpm.scenario.ProcessScenario
import org.camunda.bpm.scenario.Scenario
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@Deployment(resources = ["exam-registration.bpmn", "initial-existence-check.bpmn", "checkExamRegistrationDeadline.dmn"])
@ActiveProfiles("test")
class ExamRegistrationProcessTest {

    private val processKey = "examRegistration"

    @Mock
    private lateinit var process: ProcessScenario

    @Mock
    private lateinit var studentRepository: StudentRepository

    @Mock
    private lateinit var courseRepository: CourseRepository

    @Mock
    private lateinit var justificationRepository: JustificationRepository

    @MockBean
    private lateinit var mailService: MailService

    @Mock
    private lateinit var delegateExecution: DelegateExecution



    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Register mocks for the delegates
        listOf(
            "enrollCourseDelegate" to EnrollCourseDelegate(studentRepository, courseRepository),
            "removeJustificationDelegate" to RemoveJustificationDelegate(justificationRepository),
            "storeJustificationDelegate" to StoreJustificationDelegate(justificationRepository, studentRepository),
            "checkCourseExistsDelegate" to CheckCourseExistsDelegate(courseRepository),
            "checkEmailFormatDelegate" to CheckEmailFormatDelegate(),
            "checkEnrollmentDelegate" to CheckEnrollmentDelegate(studentRepository, courseRepository),
            "checkStudentExistsDelegate" to CheckStudentExistsDelegate(studentRepository),
            "initVariablesListener" to InitVariablesListener(),
            "statusListener" to StatusListener()
        ).forEach { (name, delegate) -> Mocks.register(name, delegate) }

        // mock mailService
        doNothing().`when`(mailService).sendConfirmation(delegateExecution)
        doNothing().`when`(mailService).sendRejection(delegateExecution)
        doNothing().`when`(mailService).sendAlreadyEnrolled(delegateExecution)
        doNothing().`when`(mailService).sendStudentNotFound(delegateExecution)
        doNothing().`when`(mailService).sendCourseNotFound(delegateExecution)
        doNothing().`when`(mailService).sendLateEnrollment(delegateExecution)

        // default scenario
        `when`(studentRepository.findByEmail((anyString()))).thenReturn(StudentEntity())
        `when`(courseRepository.findByName(anyString())).thenReturn(CourseEntity())

        `when`(process.waitsAtUserTask("userTaskCancelOrApply")).thenReturn { task ->
            task.complete(
                withVariables(
                    "cancelRegistration", false,
                    "justification", "Website not reachable",
                    "justificationType", "technical"
                )
            )
        }

        `when`(process.waitsAtUserTask("taskOfficeCheck")).thenReturn { task ->
            task.complete(
                withVariables("acceptJustification", true)
            )
        }

        `when`(process.waitsAtUserTask("technicalCheck")).thenReturn { task ->
            task.complete(
                withVariables("acceptJustification", true)
            )
        }

    }

    @AfterEach
    fun tearDown() {
        Mocks.reset()
    }

    //   HAPPY PATH
    @Test
    fun shouldSuccessfullyRegister() {
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "courseA" to true,
            "courseB" to true,
            "courseC" to false,
            "currentMonth" to "03"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_RegistrationSuccessful")
    }

    @Test
    fun shouldSuccessfullyRegisterWithManualLateEnrollment() {
        `when`(studentRepository.save(any())).thenThrow(BpmnError("SYSTEM_ERROR"))
        `when`(process.waitsAtUserTask("enrollManually")).thenReturn { task -> task.complete() }

        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "courseA" to true,
            "courseB" to true,
            "courseC" to false,
            "currentMonth" to "03"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()

        verify(process).hasFinished("EndEvent_RegistrationSuccessful")
    }

    @Test
    fun shouldNotBeInDeadlineAndStudentCancels() {
        `when`(process.waitsAtUserTask("userTaskCancelOrApply")).thenReturn { task ->
            task.complete(withVariables("cancelRegistration", true))
        }
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "courseA" to true,
            "courseB" to true,
            "courseC" to false,
            "currentMonth" to "04"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_Canceled")

    }

    @Test
    fun shouldNotBeInDeadlineAndAutoCancels() {
        `when`(process.waitsAtUserTask("userTaskCancelOrApply")).thenReturn { task ->
            task.defer("P7D", {})
        }
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "courseA" to true,
            "courseB" to true,
            "courseC" to false,
            "currentMonth" to "04"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_Stopped")

    }

    @Test
    fun shouldBeRejected() {
        `when`(process.waitsAtUserTask("taskOfficeCheck")).thenReturn { task ->
            task.complete(withVariables("acceptJustification", false))
        }
        `when`(process.waitsAtUserTask("userTaskCancelOrApply")).thenReturn { task ->
            task.complete(
                withVariables(
                    "cancelRegistration", false,
                    "justification", "Forgot laptop",
                    "justificationType", "personal"
                )
            )
        }
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "courseA" to true,
            "courseB" to true,
            "courseC" to false,
            "currentMonth" to "04"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_Rejected")

    }

    @Test
    fun shouldStopInvalidEmailFormat() {
        val variables = mapOf(
            "studentEmail" to "invalid Email",
            "courseA" to true,
            "courseB" to true,
            "courseC" to false,
            "currentMonth" to "03"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_InvalidEmailFormat")
    }

    @Test
    fun shouldStopStudentNotFound() {
        `when`(studentRepository.findByEmail((anyString()))).thenReturn(null)
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "courseA" to true,
            "courseB" to true,
            "courseC" to false,
            "currentMonth" to "03"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_StudentNotFound")

    }

    @Test
    fun shouldStopCourseNotFound() {
        `when`(courseRepository.findByName(anyString())).thenReturn(null)
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "courseA" to true,
            "courseB" to true,
            "courseC" to false,
            "currentMonth" to "03"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_CourseNotFound")

    }

    @Test
    fun shouldStopStudentAlreadyEnrolled() {
        val course = CourseEntity()
        val student = StudentEntity(courses = mutableListOf(course))
        `when`(studentRepository.findByEmail((anyString()))).thenReturn(student)
        `when`(courseRepository.findByName(anyString())).thenReturn(course)
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "courseA" to true,
            "courseB" to true,
            "courseC" to false,
            "currentMonth" to "03"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_AlreadyEnrolled")
    }
}
