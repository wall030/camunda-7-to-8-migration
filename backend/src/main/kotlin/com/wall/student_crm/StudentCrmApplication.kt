package com.wall.student_crm

import org.flywaydb.core.Flyway
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.sql.DataSource

@SpringBootApplication
class StudentCrmApplication(private val dataSource: DataSource) : CommandLineRunner {

    // manual migration, so custom tables are built after camundas tables
     override fun run(vararg args: String?) {
        Flyway.configure()
            .baselineOnMigrate(true)
            .validateOnMigrate(false)
            .dataSource(dataSource)
            .load()
            .migrate()
    }
}

fun main(args: Array<String>) {
    runApplication<StudentCrmApplication>(*args)
}