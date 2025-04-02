plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "com.wall"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web:3.3.0")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	implementation("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter-rest:7.22.0")
	implementation("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter-webapp:7.22.0")
	implementation("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter-external-task-client:7.22.0")
	implementation("org.camunda.bpm:camunda-engine-plugin-spin:7.22.0")
	implementation("org.camunda.spin:camunda-spin-dataformat-all:7.22.0")
	implementation("org.camunda.bpm:camunda-engine-rest-openapi:7.22.0")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter-test:7.22.0")
	testImplementation("org.camunda.community.process_test_coverage:camunda-process-test-coverage-starter-platform-7:2.8.0")
	testImplementation("org.assertj:assertj-core:3.25.3")
	testImplementation("org.camunda.bpm.extension.scenario:camunda-platform-scenario-runner:2.0.0.alpha.2")
	testImplementation("com.h2database:h2")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

