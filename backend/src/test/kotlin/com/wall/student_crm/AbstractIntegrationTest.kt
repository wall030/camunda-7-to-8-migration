package com.wall.student_crm

import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractIntegrationTest {

    companion object {
        val camundaContainer = PostgreSQLContainer<Nothing>("postgres:15").apply {
            withDatabaseName("camunda")
            withUsername("camunda")
            withPassword("camunda")
            start()
        }

        val businessContainer = PostgreSQLContainer<Nothing>("postgres:15").apply {
            withDatabaseName("business")
            withUsername("business")
            withPassword("business")
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            // Camunda DB
            registry.add("spring.datasource.url", camundaContainer::getJdbcUrl)
            registry.add("spring.datasource.username", camundaContainer::getUsername)
            registry.add("spring.datasource.password", camundaContainer::getPassword)
            registry.add("spring.datasource.driver-class-name", camundaContainer::getDriverClassName)

            // Business DB
            registry.add("spring.datasource.business.url", businessContainer::getJdbcUrl)
            registry.add("spring.datasource.business.username", businessContainer::getUsername)
            registry.add("spring.datasource.business.password", businessContainer::getPassword)
            registry.add("spring.datasource.business.driver-class-name", businessContainer::getDriverClassName)
        }
    }
}
