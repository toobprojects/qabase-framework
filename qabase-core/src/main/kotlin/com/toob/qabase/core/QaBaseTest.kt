package com.toob.qabase.core

import org.junit.jupiter.api.TestInstance
import kotlin.reflect.KClass

// Specifies this annotation can only be applied to classes (same as @Target(ElementType.TYPE) in Java).
@Target(AnnotationTarget.CLASS)

// Ensures the annotation is available at runtime, so Spring Boot can detect and process it.
@Retention(AnnotationRetention.RUNTIME)

// Ensures this annotation appears in generated documentation (same as Java’s @Documented).
@MustBeDocumented

// Allows non-static @BeforeAll
@TestInstance(TestInstance.Lifecycle.PER_CLASS)


/**
 * Enables constructor injection in test classes.
 * This is especially useful in Kotlin, where constructor injection is preferred
 * over field injection for better immutability and clarity.
 */
annotation class QaBaseTest(

	/**
	 * Optional Spring configuration classes.
	 * If not provided, Spring will try to locate configuration automatically.
	 */
	val classes: Array<KClass<*>> = []
)
