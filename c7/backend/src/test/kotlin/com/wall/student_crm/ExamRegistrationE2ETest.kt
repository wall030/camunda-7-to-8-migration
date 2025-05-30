package com.wall.student_crm

import com.wall.student_crm.persistence.course.StudentCourseId
import com.wall.student_crm.persistence.course.StudentCourseRepository
import com.wall.student_crm.rest.dto.RegistrationDto
import com.wall.student_crm.shared.TimeProvider
import com.wall.student_crm.service.MailService
import org.assertj.core.api.Assertions.assertThat
import org.camunda.bpm.engine.IdentityService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.Base64

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProcessE2ETest : AbstractTest() {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var identityService: IdentityService

    @Autowired
    lateinit var studentCourseRepository: StudentCourseRepository

    @MockBean
    lateinit var mailService: MailService

    @MockBean
    lateinit var timeProvider: TimeProvider

    @BeforeEach
    fun setup() {
        identityService.createUserQuery().list().forEach { user ->
            identityService.deleteUser(user.id)
        }

        // test users
        createTestUser(
            id = "st",
            email = "student@test.com",
            firstName = "student",
            lastName = "student",
            password = "st",
            group = "students"
        )

        createTestUser(
            id = "ex",
            email = "examOffice@test.com",
            firstName = "examOffice",
            lastName = "examOffice",
            password = "ex",
            group = "examoffice"
        )

        createTestUser(
            id = "ts",
            email = "technicalService@test.com",
            firstName = "technicalService",
            lastName = "technicalService",
            password = "ts",
            group = "technicalservice"
        )
    }

    private fun createTestUser(
        id: String,
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        group: String
    ) {
        val user = identityService.newUser(id).apply {
            this.email = email
            this.firstName = firstName
            this.lastName = lastName
            this.password = password
        }
        identityService.saveUser(user)
        identityService.createMembership(id, group)
    }

    private data class UserCredentials(val username: String, val password: String)

    private val student = UserCredentials("st", "st")
    private val examOffice = UserCredentials("ex", "ex")
    private val technicalService = UserCredentials("ts", "ts")

    private fun createHeaders(credentials: UserCredentials) = HttpHeaders().apply {
        val encodedCredentials = Base64.getEncoder()
            .encodeToString("${credentials.username}:${credentials.password}".toByteArray())
        set("Authorization", "Basic $encodedCredentials")
        contentType = MediaType.APPLICATION_JSON
    }

    @Test
    @Transactional
    fun shouldSuccessfullyRegister() {
        `when`(timeProvider.now()).thenReturn(LocalDate.of(2024, 3, 1))

        // Start registration process
        val registrationDto = RegistrationDto(
            studentEmail = "student@test.com",
            prerequisiteA = true,
            prerequisiteB = true,
            prerequisiteC = false,
            prerequisiteD = false,
            course = "Course A"
        )

        val request = HttpEntity(registrationDto, createHeaders(student))

        val startResponse = restTemplate.postForEntity(
            "/api/process/registerExam",
            request,
            String::class.java
        )
        assertThat(startResponse.statusCode).isEqualTo(HttpStatus.OK)
        val processInstanceId = startResponse.body!!
        println("Started process instance: $processInstanceId")


        // Query for user tasks. Successful when tasklist is empty and enrollment is stored
        val taskHeaders = createHeaders(student)
        val taskEntity = HttpEntity<Void>(taskHeaders)
        val taskResponse = restTemplate.exchange(
            "/api/tasks?processDefinitionKey=examRegistration",
            HttpMethod.GET,
            taskEntity,
            List::class.java
        )
        assertThat(taskResponse.statusCode).isEqualTo(HttpStatus.OK)
        val tasks = taskResponse.body!!

        assertThat(tasks).isEmpty()
        assertThat(studentCourseRepository.existsById(StudentCourseId("st",1))).isTrue
    }

    @Test
    @Transactional
    fun shouldBeRejected() {
        `when`(timeProvider.now()).thenReturn(LocalDate.of(2024, 4, 1))

        // Start the registration process
        val registrationDto = RegistrationDto(
            studentEmail = "student@test.com",
            prerequisiteA = true,
            prerequisiteB = true,
            prerequisiteC = false,
            prerequisiteD = false,
            course = "Course A"
        )

        val request = HttpEntity(registrationDto, createHeaders(student))

        val startResponse = restTemplate.postForEntity(
            "/api/process/registerExam",
            request,
            String::class.java
        )
        assertThat(startResponse.statusCode).isEqualTo(HttpStatus.OK)
        val processInstanceId = startResponse.body!!
        println("Started process instance: $processInstanceId")

        // Query for user tasks. Successful when tasklist is empty
        var taskHeaders = createHeaders(student)
        var taskEntity = HttpEntity<Void>(taskHeaders)
        var taskResponse = restTemplate.exchange(
            "/api/tasks?processDefinitionKey=examRegistration",
            HttpMethod.GET,
            taskEntity,
            List::class.java
        )
        assertThat(taskResponse.statusCode).isEqualTo(HttpStatus.OK)
        var tasks = taskResponse.body!!

        assertThat(tasks).isNotEmpty

        var taskMap = tasks[0] as Map<String, String>
        var taskId = taskMap["id"]!!
        println("Found user task: $taskId")

        // Claim task "userTaskCancelOrApply"
        var claimUrl = "/api/tasks/$taskId/claim?assignee=${student.username}"
        var claimRequest = HttpEntity<Void>(createHeaders(student))
        var claimResponse = restTemplate.exchange(
            claimUrl,
            HttpMethod.POST,
            claimRequest,
            String::class.java
        )
        assertThat(claimResponse.statusCode).isEqualTo(HttpStatus.OK)
        println(claimResponse.body)

        // Complete the task "userTaskCancelOrApply"
        var completeUrl = "/api/tasks/$taskId/complete"
        var completeRequest = HttpEntity(
            mapOf(
                "cancelRegistration" to false,
                "justification" to "Website was down",
                "justificationType" to "personal"
            ),
            createHeaders(student)
        )
        var completeResponse = restTemplate.postForEntity(completeUrl, completeRequest, String::class.java)
        assertThat(completeResponse.statusCode).isEqualTo(HttpStatus.OK)
        println(completeResponse.body)


        // Query for user tasks (examoffice)
        taskHeaders = createHeaders(examOffice)
        taskEntity = HttpEntity<Void>(taskHeaders)
        taskResponse = restTemplate.exchange(
            "/api/tasks?processDefinitionKey=examRegistration",
            HttpMethod.GET,
            taskEntity,
            List::class.java
        )
        assertThat(taskResponse.statusCode).isEqualTo(HttpStatus.OK)
        tasks = taskResponse.body!!

        assertThat(tasks).isNotEmpty

        taskMap = tasks[0] as Map<String, String>
        taskId = taskMap["id"]!!
        println("Found user task: $taskId")

        // Claim user task "taskOfficeCheck"
        claimUrl = "/api/tasks/$taskId/claim?assignee=${examOffice.username}"
        claimRequest = HttpEntity<Void>(createHeaders(examOffice))
        claimResponse = restTemplate.exchange(
            claimUrl,
            HttpMethod.POST,
            claimRequest,
            String::class.java
        )
        assertThat(claimResponse.statusCode).isEqualTo(HttpStatus.OK)
        println(claimResponse.body)

        // Complete the task "userTaskCancelOrApply"
        completeUrl = "/api/tasks/$taskId/complete"
        completeRequest = HttpEntity(
            mapOf("acceptJustification" to true),
            createHeaders(examOffice)
        )
        completeResponse = restTemplate.postForEntity(completeUrl, completeRequest, String::class.java)
        assertThat(completeResponse.statusCode).isEqualTo(HttpStatus.OK)
        println(completeResponse.body)
    }

}