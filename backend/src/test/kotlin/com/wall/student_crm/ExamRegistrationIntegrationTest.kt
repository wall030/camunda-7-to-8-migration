package com.wall.student_crm

import com.wall.student_crm.persistence.course.CourseEntity
import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.course.StudentCourseId
import com.wall.student_crm.persistence.course.StudentCourseRepository
import com.wall.student_crm.persistence.prerequisite.PrerequisiteEntity
import com.wall.student_crm.persistence.prerequisite.PrerequisiteRepository
import jakarta.mail.internet.MimeMessage
import org.camunda.bpm.engine.IdentityService
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables
import org.camunda.bpm.scenario.ProcessScenario
import org.camunda.bpm.scenario.Scenario
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@Deployment(resources = ["exam-registration.bpmn", "revise-course-size.bpmn", "initial-existence-check.bpmn", "checkExamRegistrationDeadline.dmn"])
@ActiveProfiles("test")
class ExamRegistrationIntegrationTest: AbstractIntegrationTest() {

    private val processKey = "examRegistration"

    @Mock
    private lateinit var process: ProcessScenario

    @Mock
    private lateinit var reviseCourseSizeProcess: ProcessScenario

    @MockBean
    lateinit var mailSender: JavaMailSender

    @Autowired
    lateinit var identityService: IdentityService

    @Autowired
    lateinit var studentCourseRepository: StudentCourseRepository

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Autowired
    lateinit var prerequisiteRepository: PrerequisiteRepository

    @BeforeEach
    fun setup() {
        prerequisiteRepository.deleteAll()
        courseRepository.deleteAll()
        identityService.deleteUser("tt")


        val prerequisiteA = PrerequisiteEntity(name = "prerequisite a")
        val prerequisiteB = PrerequisiteEntity(name = "prerequisite b")
        prerequisiteRepository.save(prerequisiteA)
        prerequisiteRepository.save(prerequisiteB)
        val course = CourseEntity(
            name = "Course X",
            currentSize = 0,
            prerequisites = mutableListOf(
                prerequisiteA,
                prerequisiteB
            )
        )
        courseRepository.save(course)

        if (identityService.createUserQuery().userId("tt").singleResult() == null) {
            val user = identityService.newUser("tt")
            user.email = "test@test.com"
            user.firstName = "test"
            user.lastName = "test"
            identityService.saveUser(user)
        }


        // user tasks
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

    @BeforeEach
    fun setupMocks() {
        val mimeMessage = mock(MimeMessage::class.java)
        `when`(mailSender.createMimeMessage()).thenReturn(mimeMessage)
        doNothing().`when`(mailSender).send(any(MimeMessage::class.java))
    }

    @Test
    fun shouldSuccessfullyRegister() {
        val studentId = "tt"
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "course" to "Course X",
            "prerequisiteA" to true,
            "prerequisiteB" to true,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "currentMonth" to "03"
        )
        Scenario.run(process)
            .startByKey(processKey, variables)
            .execute()



        verify(process).hasFinished("EndEvent_RegistrationSuccessful")
        val course = courseRepository.findByName("Course X")!!
        val isEnrolled = studentCourseRepository.existsById(StudentCourseId(studentId, course.id))
        assert(isEnrolled)

    }

    @Test
    fun shouldBeRejected() {
        `when`(process.waitsAtUserTask("technicalCheck")).thenReturn { task ->
            task.complete(
                withVariables("acceptJustification", false)
            )
        }

        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "course" to "Course X",
            "prerequisiteA" to true,
            "prerequisiteB" to true,
            "prerequisiteC" to false,
            "prerequisiteD" to false,
            "currentMonth" to "04"
        )
        Scenario.run(process)
            .startByKey(processKey, variables)
            .execute()

        verify(process).hasFinished("EndEvent_Rejected")
    }
}
