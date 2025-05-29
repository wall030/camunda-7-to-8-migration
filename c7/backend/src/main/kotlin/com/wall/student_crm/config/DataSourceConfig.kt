package com.wall.student_crm.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
class DataSourceConfig {

    @Primary
    @Bean(name = ["camundaDataSource", "dataSource"])
    fun camundaDataSource(
        @Value("\${spring.datasource.url}") url: String,
        @Value("\${spring.datasource.username}") username: String,
        @Value("\${spring.datasource.password}") password: String,
        @Value("\${spring.datasource.driver-class-name}") driverClassName: String
    ): DataSource {
        return DataSourceBuilder.create()
            .url(url)
            .username(username)
            .password(password)
            .driverClassName(driverClassName)
            .build()
    }

    @Bean(name = ["businessDataSource"])
    fun businessDataSource(
        @Value("\${spring.datasource.business.url}") url: String,
        @Value("\${spring.datasource.business.username}") username: String,
        @Value("\${spring.datasource.business.password}") password: String,
        @Value("\${spring.datasource.business.driver-class-name}") driverClassName: String
    ): DataSource {
        return DataSourceBuilder.create()
            .url(url)
            .username(username)
            .password(password)
            .driverClassName(driverClassName)
            .build()
    }
}