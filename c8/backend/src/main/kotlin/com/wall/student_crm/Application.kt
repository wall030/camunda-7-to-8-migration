package com.wall.student_crm

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.sql.DataSource

@SpringBootApplication
class Application(
    @Qualifier("camundaDataSource") private val camundaDataSource: DataSource,
    @Qualifier("businessDataSource") private val businessDataSource: DataSource
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // Camunda-DB Migration
        Flyway.configure()
            .dataSource(camundaDataSource)
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .validateOnMigrate(false)
            .load()
            .migrate()

        // Business-DB Migration
        Flyway.configure()
            .dataSource(businessDataSource)
            .locations("classpath:db/business-migration")
            .baselineOnMigrate(true)
            .validateOnMigrate(false)
            .load()
            .migrate()
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}