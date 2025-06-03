package com.wall.student_crm.service.integration

import com.wall.student_crm.config.CamundaConfig
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime
import kotlin.collections.get

@Service
class CamundaTokenService(
    private val camundaConfig: CamundaConfig
) {

    companion object {
        private val logger = LoggerFactory.getLogger(CamundaTokenService::class.java)
    }

    private val webClient = WebClient.builder().build()

    // Tasklist Token
    private var cachedTasklistToken: String? = null
    private var tasklistTokenExpiryTime: LocalDateTime? = null

    // Zeebe Token (For new Camunda REST API)
    private var cachedZeebeToken: String? = null
    private var zeebeTokenExpiryTime: LocalDateTime? = null

    fun getAccessToken(): String {
        if (cachedTasklistToken != null && tasklistTokenExpiryTime != null &&
            LocalDateTime.now().isBefore(tasklistTokenExpiryTime!!.minusMinutes(5))) {
            logger.debug("Using cached Tasklist access token")
            return cachedTasklistToken!!
        }

        logger.debug("Tasklist token expired or not cached, refreshing")
        return refreshTasklistAccessToken()
    }

    fun getZeebeAccessToken(): String {
        if (cachedZeebeToken != null && zeebeTokenExpiryTime != null &&
            LocalDateTime.now().isBefore(zeebeTokenExpiryTime!!.minusMinutes(5))) {
            logger.debug("Using cached Zeebe access token")
            return cachedZeebeToken!!
        }

        logger.debug("Zeebe token expired or not cached, refreshing")
        return refreshZeebeAccessToken()
    }

    private fun refreshTasklistAccessToken(): String {
        logger.info("Refreshing Camunda Tasklist access token")

        val formData = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "client_credentials")
            add("audience", camundaConfig.tokenAudienceTasklist)
            add("client_id", camundaConfig.clientId)
            add("client_secret", camundaConfig.clientSecret)
        }

        try {
            val response = webClient.post()
                .uri(camundaConfig.authorizationServerUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map::class.java)
                .block()

            if (response != null) {
                cachedTasklistToken = response["access_token"] as String
                val expiresIn = response["expires_in"] as? Int
                if (expiresIn != null) {
                    tasklistTokenExpiryTime = LocalDateTime.now().plusSeconds(expiresIn.toLong())
                    logger.info("Successfully obtained Camunda Tasklist access token, expires in {} seconds", expiresIn)
                } else {
                    logger.warn("No expiration time received for Tasklist access token")
                }
                return cachedTasklistToken!!
            }
        } catch (e: Exception) {
            logger.error("Failed to get Tasklist access token from Camunda: {}", e.message, e)
            throw RuntimeException("Failed to get Tasklist access token from Camunda", e)
        }

        logger.error("No Tasklist access token received from Camunda")
        throw RuntimeException("No Tasklist access token received from Camunda")
    }

    private fun refreshZeebeAccessToken(): String {
        logger.info("Refreshing Camunda Zeebe access token")

        val formData = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "client_credentials")
            add("audience", camundaConfig.tokenAudienceZeebe)
            add("client_id", camundaConfig.clientId)
            add("client_secret", camundaConfig.clientSecret)
        }

        try {
            val response = webClient.post()
                .uri(camundaConfig.authorizationServerUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map::class.java)
                .block()

            if (response != null) {
                cachedZeebeToken = response["access_token"] as String
                val expiresIn = response["expires_in"] as? Int
                if (expiresIn != null) {
                    zeebeTokenExpiryTime = LocalDateTime.now().plusSeconds(expiresIn.toLong())
                    logger.info("Successfully obtained Camunda Zeebe access token, expires in {} seconds", expiresIn)
                } else {
                    logger.warn("No expiration time received for Zeebe access token")
                }
                return cachedZeebeToken!!
            }
        } catch (e: Exception) {
            logger.error("Failed to get Zeebe access token from Camunda: {}", e.message, e)
            throw RuntimeException("Failed to get Zeebe access token from Camunda", e)
        }

        logger.error("No Zeebe access token received from Camunda")
        throw RuntimeException("No Zeebe access token received from Camunda")
    }
}