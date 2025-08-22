package com.toob.qabase

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan

/**
 * Spring Boot configuration entry point for the QA Base Core module.
 *
 * This class enables auto-configuration, scans components within the `com.toob.qabase` package,
 * and sets up beans required by the Core module.
 */

// Marks this class as a Spring Boot configuration class.
@SpringBootConfiguration
// Enables Spring Boot's auto-configuration features.
@EnableAutoConfiguration
// Ensures beans within the QA Base Core package are discovered and registered.
@ComponentScan( basePackageClasses = [QaBaseCorePackage::class] )
class QaBaseCoreModuleFactory