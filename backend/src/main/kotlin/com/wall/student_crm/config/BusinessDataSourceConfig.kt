package com.wall.student_crm.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = [
        "com.wall.student_crm.persistence.course",
        "com.wall.student_crm.persistence.justification",
        "com.wall.student_crm.persistence.prerequisite"
    ],
    entityManagerFactoryRef = "businessEntityManagerFactory",
    transactionManagerRef = "businessTransactionManager"
)
class BusinessDataSourceConfig {

    @Bean(name = ["businessEntityManagerFactory"])
    fun businessEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("businessDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        val properties = hashMapOf<String, Any>(
            "hibernate.dialect" to "org.hibernate.dialect.PostgreSQLDialect",
            "hibernate.hbm2ddl.auto" to "none",
            "hibernate.show_sql" to "false"
        )

        return builder
            .dataSource(dataSource)
            .packages(
                "com.wall.student_crm.persistence.course",
                "com.wall.student_crm.persistence.justification",
                "com.wall.student_crm.persistence.prerequisite"
            )
            .persistenceUnit("business")
            .properties(properties)
            .build()
    }

    @Bean(name = ["businessTransactionManager"])
    fun businessTransactionManager(
        @Qualifier("businessEntityManagerFactory") businessEntityManagerFactory: EntityManagerFactory
    ): PlatformTransactionManager {
        return JpaTransactionManager(businessEntityManagerFactory)
    }
}