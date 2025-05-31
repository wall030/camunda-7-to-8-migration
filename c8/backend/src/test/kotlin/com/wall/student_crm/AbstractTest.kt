package com.wall.student_crm

import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@Testcontainers
abstract class AbstractTest {

    companion object {
        @Container
        val camundaContainer = PostgreSQLContainer<Nothing>("postgres:15").apply {
            withDatabaseName("camunda")
            withUsername("camunda")
            withPassword("camunda")
        }

        @Container
        val businessContainer = PostgreSQLContainer<Nothing>("postgres:15").apply {
            withDatabaseName("business")
            withUsername("business")
            withPassword("business")
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            camundaContainer.start()
            businessContainer.start()

            registry.add("spring.datasource.url") { camundaContainer.jdbcUrl }
            registry.add("spring.datasource.username") { camundaContainer.username }
            registry.add("spring.datasource.password") { camundaContainer.password }

            registry.add("spring.datasource.business.url") { businessContainer.jdbcUrl }
            registry.add("spring.datasource.business.username") { businessContainer.username }
            registry.add("spring.datasource.business.password") { businessContainer.password }
        }
    }
}
