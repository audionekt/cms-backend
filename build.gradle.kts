import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("plugin.jpa") version "1.9.20"
    jacoco
}

group = "com.cms"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    
    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // Database
    runtimeOnly("org.postgresql:postgresql")
    
    // AWS S3
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.600")
    
    // Flyway for database migrations
    implementation("org.flywaydb:flyway-core")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("com.h2database:h2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            // Overall project coverage must be 80%
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
        rule {
            // Per-class coverage (excludes infrastructure/data classes)
            element = "CLASS"
            limit {
                minimum = "0.60".toBigDecimal()
            }
            excludes = listOf(
                "com.cms.backend.CmsBackendApplicationKt",
                "com.cms.backend.config.*",
                "com.cms.backend.dto.*",
                "com.cms.backend.entity.*",
                "com.cms.backend.exception.*"  // Exception classes are infrastructure
            )
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}





