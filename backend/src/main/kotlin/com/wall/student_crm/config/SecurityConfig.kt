package com.wall.student_crm.config

import org.camunda.bpm.engine.rest.security.auth.ProcessEngineAuthenticationFilter
import org.camunda.bpm.engine.rest.security.auth.impl.HttpBasicAuthenticationProvider
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityConfig {

    @Bean
    fun authenticationFilter(): FilterRegistrationBean<ProcessEngineAuthenticationFilter> {
        val registration = FilterRegistrationBean(ProcessEngineAuthenticationFilter())
        registration.setName("camunda-auth")
        registration.addInitParameter(
            "authentication-provider",
            HttpBasicAuthenticationProvider::class.java.name
        )
//        registration.addUrlPatterns("/engine-rest/*")
        registration.addUrlPatterns("/api/*")
        return registration
    }
}