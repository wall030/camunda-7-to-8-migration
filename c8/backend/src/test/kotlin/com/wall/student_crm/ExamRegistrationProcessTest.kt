package com.wall.student_crm

import com.wall.student_crm.camunda.delegates.EnrollCourseDelegate
import com.wall.student_crm.camunda.delegates.IncreaseCourseSizeDelegate
import com.wall.student_crm.camunda.delegates.RemoveJustificationDelegate
import com.wall.student_crm.camunda.delegates.StoreJustificationDelegate
import com.wall.student_crm.camunda.delegates.validation.CheckCourseExistsDelegate
import com.wall.student_crm.camunda.delegates.CheckCourseFullDelegate
import com.wall.student_crm.camunda.delegates.validation.CheckEmailFormatDelegate
import com.wall.student_crm.camunda.delegates.validation.CheckEnrollmentDelegate
import com.wall.student_crm.camunda.delegates.validation.CheckStudentExistsDelegate
import com.wall.student_crm.camunda.listeners.InitVariablesListener
import com.wall.student_crm.camunda.listeners.StatusListener
import com.wall.student_crm.service.CamundaUserService
import com.wall.student_crm.persistence.course.CourseEntity
import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.course.StudentCourseId
import com.wall.student_crm.persistence.course.StudentCourseRepository
import com.wall.student_crm.persistence.justification.JustificationEntity
import com.wall.student_crm.persistence.justification.JustificationRepository
import com.wall.student_crm.persistence.prerequisite.PrerequisiteEntity
import com.wall.student_crm.shared.TimeProvider
import com.wall.student_crm.service.MailService
import org.camunda.bpm.engine.RuntimeService
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
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.util.Optional


@Deployment(resources = ["exam-registration.bpmn", "revise-course-size.bpmn", "initial-existence-check.bpmn", "checkExamRegistrationDeadline.dmn"])
@ActiveProfiles("test")
class ExamRegistrationProcessTest : AbstractTest() {

    private val processKey = "examRegistration"

    @Mock
    private lateinit var process: ProcessScenario

    @Mock
    private lateinit var reviseCourseSizeProcess: ProcessScenario

    @Mock
    private lateinit var camundaUserService: CamundaUserService

    @Mock
    private lateinit var courseRepository: CourseRepository

    @Mock
    private lateinit var studentCourseRepository: StudentCourseRepository

    @Mock
    private lateinit var justificationRepository: JustificationRepository

    @Mock
    private lateinit var runtimeService: RuntimeService

    @MockBean
    private lateinit var timeProvider: TimeProvider

    @MockBean
    private lateinit var mailService: MailService

    @Mock
    private lateinit var delegateExecution: DelegateExecution


    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Register mocks for the delegates
        listOf(
            "enrollCourseDelegate" to EnrollCourseDelegate(
                camundaUserService, courseRepository, studentCourseRepository
            ),
            "removeJustificationDelegate" to RemoveJustificationDelegate(justificationRepository),
            "storeJustificationDelegate" to StoreJustificationDelegate(justificationRepository, camundaUserService),
            "checkCourseExistsDelegate" to CheckCourseExistsDelegate(courseRepository),
            "checkEmailFormatDelegate" to CheckEmailFormatDelegate(),
            "increaseCourseSizeDelegate" to IncreaseCourseSizeDelegate(courseRepository),
            "checkEnrollmentDelegate" to CheckEnrollmentDelegate(
                camundaUserService,
                courseRepository,
                studentCourseRepository
            ),
            "checkStudentExistsDelegate" to CheckStudentExistsDelegate(camundaUserService),
            "checkCourseFullDelegate" to CheckCourseFullDelegate(courseRepository, runtimeService),
            "initVariablesListener" to InitVariablesListener(courseRepository, timeProvider),
            "statusListener" to StatusListener()
        ).forEach { (name, delegate) -> Mocks.register(name, delegate) }

        // mock mailService
        doNothing().`when`(mailService).sendConfirmation(delegateExecution)
        doNothing().`when`(mailService).sendRejection(delegateExecution)
        doNothing().`when`(mailService).sendAlreadyEnrolled(delegateExecution)
        doNothing().`when`(mailService).sendStudentNotFound(delegateExecution)
        doNothing().`when`(mailService).sendCourseNotFound(delegateExecution)
        doNothing().`when`(mailService).sendPrerequisitesNotMet(delegateExecution)

        // default scenario
        val justification = JustificationEntity(studentId = "studentId", justification = "Website Down")
        `when`(justificationRepository.save(any())).thenReturn(justification)
        `when`(justificationRepository.findById(any())).thenReturn(Optional.of(justification))
        `when`(camundaUserService.getUserIdByEmail(anyString())).thenReturn("studentId")
        `when`(courseRepository.findByName(anyString())).thenReturn(
            CourseEntity(
                currentSize = 5,
                maxSize = 20,
                prerequisites = mutableListOf(
                    PrerequisiteEntity(name = "prerequisite a"),
                    PrerequisiteEntity(name = "prerequisite b")
                )
            )
        )
        `when`(timeProvider.now()).thenReturn(LocalDate.of(2025, 3, 1))
        `when`(runtimeService.startProcessInstanceByMessage(eq("startReviseCourseSize"))).thenReturn(null)

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

        `when`(process.waitsAtUserTask("reviewOverbooking")).thenReturn { task ->
            task.complete(
                withVariables("overbooked", true)
            )
        }

        `when`(process.waitsAtServiceTask("generateQrCodeTask")).thenReturn { task ->
            task.complete(
                withVariables("qrCodeUrl", "QrCodeString")
            )
        }

        `when`(process.runsCallActivity("reviseCourseSizeCallActivity")).thenReturn(Scenario.use(reviseCourseSizeProcess))

        `when`(reviseCourseSizeProcess.waitsAtUserTask("checkChangeCourseSize")).thenReturn { task ->
            task.complete(withVariables("courseSizeCanBeIncreased", true))
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
            "prerequisiteA" to true,
            "prerequisiteB" to true,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "course" to "Course A"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_RegistrationSuccessful")
    }

    @Test
    fun shouldSuccessfullyRegisterWithOverbooking() {
        `when`(courseRepository.findByName(anyString())).thenReturn(
            CourseEntity(
                currentSize = 20,
                maxSize = 20,
                prerequisites = mutableListOf(
                    PrerequisiteEntity(name = "prerequisite a"),
                    PrerequisiteEntity(name = "prerequisite b")
                )
            )
        )

        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "prerequisiteA" to true,
            "prerequisiteB" to true,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "course" to "Course A",
            "currentMonth" to "03"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()

        verify(runtimeService).startProcessInstanceByMessage("startReviseCourseSize", mapOf("course" to "Course A"))
        verify(process).hasFinished("EndEvent_RegistrationSuccessful")
    }

    @Test
    fun shouldSuccessfullyRegisterWithJustificationAccepted() {
        `when`(timeProvider.now()).thenReturn(LocalDate.of(2025, 4, 1))
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "prerequisiteA" to true,
            "prerequisiteB" to true,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "course" to "Course A"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()

        verify(process).hasFinished("EndEvent_RegistrationSuccessful")
    }

    @Test
    fun shouldBeRejectedWithNoOverbooking() {
        `when`(courseRepository.findByName(anyString())).thenReturn(
            CourseEntity(
                currentSize = 20,
                maxSize = 20,
                prerequisites = mutableListOf(
                    PrerequisiteEntity(name = "prerequisite a"),
                    PrerequisiteEntity(name = "prerequisite b")
                )
            )
        )
        `when`(reviseCourseSizeProcess.waitsAtUserTask("checkChangeCourseSize")).thenReturn { task ->
            task.complete(
                withVariables("courseSizeCanBeIncreased", false)
            )
        }
        `when`(process.waitsAtUserTask("reviewOverbooking")).thenReturn { task ->
            task.complete(
                withVariables("overbooked", false)
            )
        }

        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "prerequisiteA" to true,
            "prerequisiteB" to true,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "course" to "Course A"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()

        verify(runtimeService).startProcessInstanceByMessage("startReviseCourseSize", mapOf("course" to "Course A"))
        verify(process).hasFinished("EndEvent_NoOverbooking")
    }

    @Test
    fun shouldNotBeInDeadlineAndStudentCancels() {
        `when`(timeProvider.now()).thenReturn(LocalDate.of(2025, 4, 1))
        `when`(process.waitsAtUserTask("userTaskCancelOrApply")).thenReturn { task ->
            task.complete(withVariables("cancelRegistration", true))
        }
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "prerequisiteA" to true,
            "prerequisiteB" to true,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "course" to "Course A"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_Canceled")

    }

    @Test
    fun shouldNotBeInDeadlineAndAutoCancels() {
        `when`(timeProvider.now()).thenReturn(LocalDate.of(2025, 4, 1))
        `when`(process.waitsAtUserTask("userTaskCancelOrApply")).thenReturn { task ->
            task.defer("P7D", {})
        }
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "prerequisiteA" to true,
            "prerequisiteB" to true,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "course" to "Course A"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_Stopped")

    }

    @Test
    fun shouldBeRejected() {
        `when`(timeProvider.now()).thenReturn(LocalDate.of(2025, 4, 1))
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
            "prerequisiteA" to true,
            "prerequisiteB" to true,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "course" to "Course A"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_Rejected")

    }

    @Test
    fun shouldStopInvalidEmailFormat() {
        val variables = mapOf(
            "studentEmail" to "invalid Email",
            "prerequisiteA" to true,
            "prerequisiteB" to true,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "course" to "Course A"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_InvalidEmailFormat")
    }

    @Test
    fun shouldStopStudentNotFound() {
        `when`(camundaUserService.getUserIdByEmail((anyString()))).thenReturn(null)
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "prerequisiteA" to true,
            "prerequisiteB" to true,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "course" to "Course A"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_StudentNotFound")

    }

    @Test
    fun shouldStopCourseNotFound() {
        `when`(courseRepository.findByName(anyString())).thenReturn(null)
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "prerequisiteA" to true,
            "prerequisiteB" to true,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "course" to "Course X"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_CourseNotFound")

    }

    @Test
    fun shouldStopStudentAlreadyEnrolled() {
        val course = CourseEntity()
        val userId = "id"
        `when`(camundaUserService.getUserIdByEmail((anyString()))).thenReturn(userId)
        `when`(courseRepository.findByName(anyString())).thenReturn(course)
        `when`(studentCourseRepository.existsById(StudentCourseId(userId, course.id))).thenReturn(true)
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "prerequisiteA" to true,
            "prerequisiteB" to true,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "course" to "Course A"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_AlreadyEnrolled")
    }

    @Test
    fun shouldStopPrerequisiteNotMet() {
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "prerequisiteA" to true,
            "prerequisiteB" to false,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "course" to "Course A"
        )
        Scenario.run(process).startByKey(processKey, variables).execute()
        verify(process).hasFinished("EndEvent_PrerequisitesNotMet")
    }
}