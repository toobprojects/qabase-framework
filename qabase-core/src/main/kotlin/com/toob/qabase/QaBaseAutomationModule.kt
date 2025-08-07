package com.toob.qabase

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Import

// Specifies this annotation can only be applied to classes (same as @Target(ElementType.TYPE) in Java).
@Target(AnnotationTarget.CLASS)

// Ensures the annotation is available at runtime, so Spring Boot can detect and process it.
@Retention(AnnotationRetention.RUNTIME)

// Ensures this annotation appears in generated documentation (same as Java’s @Documented).
@MustBeDocumented

// Marks the class as a Spring Boot config class (like @Configuration, but for bootstrapping Spring Boot).
@SpringBootConfiguration

// Enables Spring Boot’s auto-configuration magic — detects and configures beans based on classpath.
@EnableAutoConfiguration

// Explicitly imports QABase’s Spring factory class, so all core beans (like config, Allure setup, etc.) are loaded into the Spring context.
@Import(QaBaseCoreModuleFactory::class)
annotation class QaBaseAutomationModule()
