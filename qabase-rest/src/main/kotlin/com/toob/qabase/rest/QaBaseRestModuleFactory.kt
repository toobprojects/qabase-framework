package com.toob.qabase.rest

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

/**
 * Spring Boot configuration entry point for the QA Base REST module.
 *
 * This class:
 * - Enables auto-configuration for the module.
 * - Scans for components in the `com.toob.qabase.http` package.
 * - Loads and binds configuration properties from [RestModuleConfigs].
 */
@SpringBootConfiguration
// Marks this class as a Spring Boot configuration class.
@EnableAutoConfiguration
// Enables Spring Boot's auto-configuration mechanism.
@ComponentScan( basePackageClasses = [QaBaseRestPackage::class] )
// Ensures beans in the QA Base REST package are discovered.
@EnableConfigurationProperties(RestModuleConfigs::class)
// Binds configuration properties from RestModuleConfigs.
class QaBaseRestModuleFactory