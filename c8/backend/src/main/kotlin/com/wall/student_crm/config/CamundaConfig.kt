package com.wall.student_crm.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class CamundaConfig {

    @Value("\${camunda.client.auth.client-id}")
    lateinit var clientId: String

    @Value("\${camunda.client.auth.client-secret}")
    lateinit var clientSecret: String

    @Value("\${camunda.client.authorization.server.url}")
    lateinit var authorizationServerUrl: String

    @Value("\${camunda.client.token.audience.tasklist}")
    lateinit var tokenAudienceTasklist: String

    @Value("\${camunda.client.token.audience.zeebe}")
    lateinit var tokenAudienceZeebe: String

    @Value("\${camunda.tasklist.api.url}")
    lateinit var tasklistApiUrl: String

    @Value("\${camunda.rest.api.url}")
    lateinit var restApiUrl: String


}