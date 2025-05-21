package com.wall.student_crm

import com.wall.student_crm.persistence.course.CourseEntity
import com.wall.student_crm.persistence.course.CourseRepository
import com.wall.student_crm.persistence.prerequisite.PrerequisiteRepository
import com.wall.student_crm.persistence.student.StudentEntity
import com.wall.student_crm.persistence.student.StudentRepository
import jakarta.mail.internet.MimeMessage
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables
import org.camunda.bpm.scenario.ProcessScenario
import org.camunda.bpm.scenario.Scenario
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@Deployment(resources = ["exam-registration.bpmn", "revise-course-size.bpmn", "initial-existence-check.bpmn", "checkExamRegistrationDeadline.dmn"])
@ActiveProfiles("test")
@Transactional
class ExamRegistrationIntegrationTest {

    private val processKey = "examRegistration"

    @Mock
    private lateinit var process: ProcessScenario

    @Mock
    private lateinit var reviseCourseSizeProcess: ProcessScenario

    @MockBean
    lateinit var mailSender: JavaMailSender

    @Autowired
    lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Autowired
    lateinit var prerequisiteRepository: PrerequisiteRepository

    @BeforeEach
    fun setup() {
        val prerequisiteA = prerequisiteRepository.findByName("prerequisite a")!!
        val prerequisiteB = prerequisiteRepository.findByName("prerequisite b")!!
        val student = StudentEntity(email = "test@test.com", courses = mutableListOf())
        val course = CourseEntity(
            name = "Course A", currentSize = 0,
            prerequisites = mutableListOf(
                prerequisiteA,
                prerequisiteB
            )
        )

        // cant use deleteAll users because of membership referencing admin user
        studentRepository.delete(student)
        courseRepository.deleteAll()


        courseRepository.save(course)
        studentRepository.save(student)


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
        val variables = mapOf(
            "studentEmail" to "test@test.com",
            "course" to "Course A",
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

        val student = studentRepository.findByEmail("test@test.com")!!
        assert(student.courses.any { it.name == "Course A" })

    }
}
