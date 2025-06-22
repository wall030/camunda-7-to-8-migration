package com.wall.student_crm

import io.camunda.zeebe.client.ZeebeClient
import io.camunda.zeebe.client.api.command.DeployResourceCommandStep1.DeployResourceCommandStep2
import io.camunda.zeebe.client.api.response.DeploymentEvent
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent
import io.camunda.zeebe.client.api.response.PublishMessageResponse
import io.camunda.zeebe.process.test.api.ZeebeTestEngine
import io.camunda.zeebe.process.test.assertions.BpmnAssert
import io.camunda.zeebe.process.test.extension.ZeebeProcessTest
import org.junit.jupiter.api.fail
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.util.concurrent.TimeoutException
import kotlin.math.max

@ZeebeProcessTest
@ActiveProfiles("test")
abstract class AbstractTest {

    lateinit var engine: ZeebeTestEngine
    lateinit var client: ZeebeClient

    fun deployResources(vararg resources: String?): DeploymentEvent? {
        val commandStep1 = client.newDeployResourceCommand()

        var commandStep2: DeployResourceCommandStep2? = null
        for (process in resources) {
            if (commandStep2 == null) {
                commandStep2 = commandStep1.addResourceFromClasspath(process)
            } else {
                commandStep2 = commandStep2.addResourceFromClasspath(process)
            }
        }

        return commandStep2!!.send().join()
    }


    private fun waitForIdleState(duration: Duration?) {
        engine.waitForIdleState(duration)
    }

    private fun waitForBusyState(duration: Duration?) {
        engine.waitForBusyState(duration)
    }

    fun sendMessage(
        messageName: String, correlationKey: String, variables: Map<String, Any>
    ): PublishMessageResponse {

        val timeToLive = Duration.ZERO

        val response: PublishMessageResponse =
            client
                .newPublishMessageCommand()
                .messageName(messageName)
                .correlationKey(correlationKey)
                .variables(variables)
                .timeToLive(timeToLive)
                .send()
                .join()
        waitForIdleState(Duration.ofSeconds(1))
        return response
    }

    fun increaseTime(duration: Duration?) {

        waitForIdleState(Duration.ofSeconds(1))

        engine.increaseTime(duration)

        try {
            waitForBusyState(Duration.ofSeconds(1))
            waitForIdleState(Duration.ofSeconds(1))
        } catch (e: TimeoutException) {

        }
    }


    fun completeServiceTask(jobType: String) {
        completeServiceTasks(jobType, 1)
    }

    fun completeServiceTask(jobType: String, variables: Map<String, Any>) {
        completeServiceTasks(jobType, 1, variables)
    }

    fun completeServiceTasks(jobType: String, count: Int) {
        completeServiceTasks(jobType, count, emptyMap())
    }

    fun completeServiceTasks(jobType: String, count: Int, variables: Map<String, Any>) {
        val jobs = client.newActivateJobsCommand()
            .jobType(jobType)
            .maxJobsToActivate(count)
            .send()
            .join()
            .jobs

        require(jobs.size == count) { "Unable to activate $count jobs, only ${jobs.size} were activated." }

        jobs.forEach { job ->
            client.newCompleteCommand(job.key)
                .variables(variables)
                .send()
                .join()
        }

        waitForIdleState(Duration.ofSeconds(1))
    }

    fun throwError(jobType: String, errorCode: String, count: Int) {
        val jobs = client.newActivateJobsCommand()
            .jobType(jobType)
            .maxJobsToActivate(count)
            .send()
            .join()
            .jobs

        require(jobs.size == count) { "Unable to activate $count jobs, only ${jobs.size} were activated." }

        jobs.forEach { job ->
            client.newThrowErrorCommand(job.key)
                .errorCode(errorCode)
                .send()
                .join()
        }

        waitForIdleState(Duration.ofSeconds(1))
    }

    fun completeUserTask(elementId: String, variables: Map<String, Any> = emptyMap()) {

        val activateJobsResponse = client
            .newActivateJobsCommand()
            .jobType("io.camunda.zeebe:userTask")
            .maxJobsToActivate(1000)
            .timeout(Duration.ofSeconds(30))
            .send()
            .join()

        println("Found ${activateJobsResponse.jobs.size} user tasks")
        activateJobsResponse.jobs.forEach { job ->
            println("User task: ${job.elementId}")
        }

        var userTaskWasCompleted = false

        for (userTask in activateJobsResponse.jobs) {
            if (userTask.elementId == elementId) {
                client.newCompleteCommand(userTask.key)
                    .variables(variables)
                    .send()
                    .join()
                userTaskWasCompleted = true
                println("Completed user task: ${userTask.elementId}")
            } else {
                client.newFailCommand(userTask.key)
                    .retries(max(userTask.retries, 1))
                    .send()
                    .join()
                println("Failed user task: ${userTask.elementId}")
            }
        }

        waitForIdleState(Duration.ofSeconds(1))

        if (!userTaskWasCompleted) {
            fail("Tried to complete task $elementId, but it was not found. Available tasks: ${activateJobsResponse.jobs.map { it.elementId }}")
        }
    }

    fun startInstance(id: String, variables: Map<String, Any>): ProcessInstanceEvent {
        val processInstance = client.newCreateInstanceCommand()
            .bpmnProcessId(id)
            .latestVersion()
            .variables(variables)
            .send().join()

        BpmnAssert.assertThat(processInstance)
            .isStarted()

        return processInstance
    }

}
