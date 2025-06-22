package com.wall.student_crm

import io.camunda.zeebe.process.test.assertions.BpmnAssert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

class ExamRegistrationProcessUnitTest : AbstractTest() {

    private val processKey = "examRegistration"

    @BeforeEach
    fun setup() {

        val deploymentEvent = deployResources(
            // BPMNs
            "exam-registration.bpmn",
            "revise-course-size.bpmn",
            "initial-existence-check.bpmn",
            // DMNs
            "checkExamRegistrationDeadline.dmn",
            // Forms
            "form-checkChangeCourseSize.form",
            "form-reviewOverbooking.form",
            "form-start-exam-registration.form",
            "form-taskOfficeCheck.form",
            "form-technicalCheck.form",
            "form-userTaskCancelOrApply.form"
        )


    }


    //   HAPPY PATH
    @Test
    fun shouldSuccessfullyRegister() {

        val processInstance = startInstance(processKey, emptyMap())

        BpmnAssert.assertThat(processInstance).isStarted()

        completeServiceTask(
            "init-variables",
            mapOf(
                "prerequisitesList" to listOf("prerequisite a", "prerequisite b"),
                "currentMonth" to "03"
            )
        )
        completeServiceTask("update-status")
        completeServiceTask("check-email-format")
        completeServiceTask("check-course-exists")
        completeServiceTask("check-student-exists")
        completeServiceTask("check-enrollment")
        completeServiceTask("check-prerequisites")
        completeServiceTask("check-prerequisites")
        completeServiceTask("check-course-full", mapOf("courseIsFull" to false))
        completeServiceTask("enroll-course")
        completeServiceTask("generateQrCode")
        completeServiceTask("update-status")
        completeServiceTask("send-mail")

        BpmnAssert.assertThat(processInstance).hasPassedElement("EndEvent_RegistrationSuccessful").isCompleted()
    }


    @Test
    fun shouldSuccessfullyRegisterWithOverbooking() {

        val processInstance = startInstance(processKey, emptyMap())

        BpmnAssert.assertThat(processInstance).isStarted()

        completeServiceTask(
            "init-variables",
            mapOf(
                "prerequisitesList" to listOf("prerequisite a", "prerequisite b"),
                "currentMonth" to "03"
            )
        )
        completeServiceTask("update-status")
        completeServiceTask("check-email-format")
        completeServiceTask("check-course-exists")
        completeServiceTask("check-student-exists")
        completeServiceTask("check-enrollment")
        completeServiceTask("check-prerequisites")
        completeServiceTask("check-prerequisites")
        completeServiceTask("check-course-full", mapOf("courseIsFull" to true))

        // Start revise-course-size process because course is full
        sendMessage("startReviseCourseSize", "", emptyMap())
        completeUserTask("checkChangeCourseSize", mapOf("courseSizeCanBeIncreased" to true))

        completeServiceTask("increase-course-size")
        completeUserTask("reviewOverbooking", mapOf("overbooked" to true))
        completeServiceTask("enroll-course")
        completeServiceTask("generateQrCode")
        completeServiceTask("update-status")
        completeServiceTask("send-mail")

        BpmnAssert.assertThat(processInstance).hasPassedElement("EndEvent_RegistrationSuccessful").isCompleted()
    }

    @Test
    fun shouldSuccessfullyRegisterWithJustificationAccepted() {

        val processInstance = startInstance(processKey, emptyMap())

        BpmnAssert.assertThat(processInstance).isStarted()

        completeServiceTask(
            "init-variables",
            mapOf(
                "prerequisitesList" to listOf("prerequisite a", "prerequisite b"),
                "currentMonth" to "04",
                // Just add email here for assignment in userTaskCancelOrApply because process is started without form in test
                "studentEmail" to "test@test.com"
            )
        )
        completeServiceTask("update-status")
        completeServiceTask("check-email-format")
        completeServiceTask("check-course-exists")
        completeServiceTask("check-student-exists")
        completeServiceTask("check-enrollment")
        completeServiceTask("check-prerequisites")
        completeServiceTask("check-prerequisites")
        completeServiceTask("update-status")
        completeUserTask(
            "userTaskCancelOrApply", mapOf(
                "cancelRegistration" to false,
                "justification" to "Website not reachable",
                "justificationType" to "technical"
            )
        )
        completeServiceTask("update-status")
        completeServiceTask("store-justification")
        completeUserTask("technicalCheck", mapOf("acceptJustification" to true))
        completeServiceTask("remove-justification")
        completeServiceTask("check-course-full", mapOf("courseIsFull" to false))
        completeServiceTask("enroll-course")
        completeServiceTask("generateQrCode")
        completeServiceTask("update-status")
        completeServiceTask("send-mail")

        BpmnAssert.assertThat(processInstance).hasPassedElement("EndEvent_RegistrationSuccessful").isCompleted()
    }

    @Test
    fun shouldBeRejectedWithNoOverbooking() {

        val processInstance = startInstance(processKey, emptyMap())

        BpmnAssert.assertThat(processInstance).isStarted()

        completeServiceTask(
            "init-variables",
            mapOf(
                "prerequisitesList" to listOf("prerequisite a", "prerequisite b"),
                "currentMonth" to "03"
            )
        )
        completeServiceTask("update-status")
        completeServiceTask("check-email-format")
        completeServiceTask("check-course-exists")
        completeServiceTask("check-student-exists")
        completeServiceTask("check-enrollment")
        completeServiceTask("check-prerequisites")
        completeServiceTask("check-prerequisites")
        completeServiceTask("check-course-full", mapOf("courseIsFull" to true))
        completeUserTask("reviewOverbooking", mapOf("overbooked" to false))
        completeServiceTask("update-status")
        completeServiceTask("send-mail")

        BpmnAssert.assertThat(processInstance).hasPassedElement("EndEvent_NoOverbooking").isCompleted()
    }

    @Test
    fun shouldNotBeInDeadlineAndStudentCancels() {

        val processInstance = startInstance(processKey, emptyMap())

        BpmnAssert.assertThat(processInstance).isStarted()

        completeServiceTask(
            "init-variables",
            mapOf(
                "prerequisitesList" to listOf("prerequisite a", "prerequisite b"),
                "currentMonth" to "04",
                "studentEmail" to "test@test.com"
            )
        )
        completeServiceTask("update-status")
        completeServiceTask("check-email-format")
        completeServiceTask("check-course-exists")
        completeServiceTask("check-student-exists")
        completeServiceTask("check-enrollment")
        completeServiceTask("check-prerequisites")
        completeServiceTask("check-prerequisites")
        completeServiceTask("update-status")
        completeUserTask("userTaskCancelOrApply", mapOf("cancelRegistration" to true))
        completeServiceTask("update-status")

        BpmnAssert.assertThat(processInstance).hasPassedElement("EndEvent_Canceled").isCompleted()
    }

    @Test
    fun shouldNotBeInDeadlineAndAutoCancels() {

        val processInstance = startInstance(processKey, emptyMap())

        BpmnAssert.assertThat(processInstance).isStarted()

        completeServiceTask(
            "init-variables",
            mapOf(
                "prerequisitesList" to listOf("prerequisite a", "prerequisite b"),
                "currentMonth" to "04",
                "studentEmail" to "test@test.com"
            )
        )
        completeServiceTask("update-status")
        completeServiceTask("check-email-format")
        completeServiceTask("check-course-exists")
        completeServiceTask("check-student-exists")
        completeServiceTask("check-enrollment")
        completeServiceTask("check-prerequisites")
        completeServiceTask("check-prerequisites")
        completeServiceTask("update-status")
        increaseTime(Duration.ofDays(7))
        completeServiceTask("update-status")

        BpmnAssert.assertThat(processInstance).hasPassedElement("EndEvent_Stopped").isCompleted()
    }

    @Test
    fun shouldBeRejected() {

        val processInstance = startInstance(processKey, emptyMap())

        BpmnAssert.assertThat(processInstance)
            .isStarted()

        completeServiceTask(
            "init-variables",
            mapOf(
                "prerequisitesList" to listOf("prerequisite a", "prerequisite b"),
                "currentMonth" to "04",
                "studentEmail" to "test@test.com"
            )
        )
        completeServiceTask("update-status")
        completeServiceTask("check-email-format")
        completeServiceTask("check-course-exists")
        completeServiceTask("check-student-exists")
        completeServiceTask("check-enrollment")
        completeServiceTask("check-prerequisites")
        completeServiceTask("check-prerequisites")
        completeServiceTask("update-status")
        completeUserTask(
            "userTaskCancelOrApply", mapOf(
                "cancelRegistration" to false,
                "justification" to "Website not reachable",
                "justificationType" to "technical"
            )
        )
        completeServiceTask("update-status")
        completeServiceTask("store-justification")
        completeUserTask("technicalCheck", mapOf("acceptJustification" to false))
        completeServiceTask("update-status")
        completeServiceTask("send-mail")

        BpmnAssert.assertThat(processInstance).hasPassedElement("EndEvent_Rejected").isCompleted()
    }

    @Test
    fun shouldStopInvalidEmailFormat() {

        val processInstance = startInstance(processKey, emptyMap())

        BpmnAssert.assertThat(processInstance).isStarted()

        completeServiceTask(
            "init-variables",
            mapOf(
                "prerequisitesList" to listOf("prerequisite a", "prerequisite b"),
                "currentMonth" to "04"
            )
        )
        completeServiceTask("update-status")
        throwError("check-email-format", "INVALID_EMAIL_FORMAT", 1)
        BpmnAssert.assertThat(processInstance).hasPassedElement("EndEvent_InvalidEmailFormat").isCompleted()
    }

    @Test
    fun shouldStopStudentNotFound() {

        val processInstance = startInstance(processKey, emptyMap())

        BpmnAssert.assertThat(processInstance).isStarted()

        completeServiceTask(
            "init-variables",
            mapOf(
                "prerequisitesList" to listOf("prerequisite a", "prerequisite b"),
                "currentMonth" to "04"
            )
        )
        completeServiceTask("update-status")
        completeServiceTask("check-email-format")
        throwError("check-student-exists", "STUDENT_NOT_FOUND", 1)
        completeServiceTask("send-mail")
        BpmnAssert.assertThat(processInstance).hasPassedElement("EndEvent_StudentNotFound").isCompleted()
    }

    @Test
    fun shouldStopCourseNotFound() {

        val processInstance = startInstance(processKey, emptyMap())

        BpmnAssert.assertThat(processInstance).isStarted()

        completeServiceTask(
            "init-variables",
            mapOf(
                "prerequisitesList" to listOf("prerequisite a", "prerequisite b"),
                "currentMonth" to "04"
            )
        )
        completeServiceTask("update-status")
        completeServiceTask("check-email-format")
        throwError("check-course-exists", "COURSE_NOT_FOUND", 1)
        completeServiceTask("send-mail")
        BpmnAssert.assertThat(processInstance).hasPassedElement("EndEvent_CourseNotFound").isCompleted()
    }

    @Test
    fun shouldStopStudentAlreadyEnrolled() {

        val processInstance = startInstance(processKey, emptyMap())

        BpmnAssert.assertThat(processInstance).isStarted()

        completeServiceTask(
            "init-variables",
            mapOf(
                "prerequisitesList" to listOf("prerequisite a", "prerequisite b"),
                "currentMonth" to "04"
            )
        )
        completeServiceTask("update-status")
        completeServiceTask("check-email-format")
        completeServiceTask("check-student-exists")
        completeServiceTask("check-course-exists")
        throwError("check-enrollment", "ALREADY_ENROLLED", 1)
        completeServiceTask("send-mail")
        BpmnAssert.assertThat(processInstance).hasPassedElement("EndEvent_AlreadyEnrolled").isCompleted()
    }

    @Test
    fun shouldStopPrerequisiteNotMet() {

        val processInstance = startInstance(processKey, emptyMap())

        BpmnAssert.assertThat(processInstance).isStarted()

        completeServiceTask(
            "init-variables",
            mapOf(
                "prerequisitesList" to listOf("prerequisite a", "prerequisite b"),
                "currentMonth" to "04"
            )
        )
        completeServiceTask("update-status")
        completeServiceTask("check-email-format")
        completeServiceTask("check-student-exists")
        completeServiceTask("check-course-exists")
        completeServiceTask("check-enrollment")
        throwError("check-prerequisites", "PREREQUISITES_NOT_MET", 1)
        completeServiceTask("send-mail")
        BpmnAssert.assertThat(processInstance).hasPassedElement("EndEvent_PrerequisitesNotMet").isCompleted()
    }


}