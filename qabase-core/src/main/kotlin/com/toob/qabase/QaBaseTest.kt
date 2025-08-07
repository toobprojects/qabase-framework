package com.toob.qabase

import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.TestConstructor
import kotlin.reflect.KClass

// Specifies this annotation can only be applied to classes (same as @Target(ElementType.TYPE) in Java).
@Target(AnnotationTarget.CLASS)

// Ensures the annotation is available at runtime, so Spring Boot can detect and process it.
@Retention(AnnotationRetention.RUNTIME)

// Ensures this annotation appears in generated documentation (same as Java’s @Documented).
@MustBeDocumented

// Extension for the @SpringBootTest
@SpringBootTest

// Allows non-static @BeforeAll
@TestInstance(TestInstance.Lifecycle.PER_CLASS)


/**
 * Enables constructor injection in test classes.
 * This is especially useful in Kotlin, where constructor injection is preferred
 * over field injection for better immutability and clarity.
 */
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
annotation class QaBaseTest(


	/**
	 * It declares that the classes parameter in custom annotation (e.g. @QaBaseTest)
	 * ... is an alias for the classes attribute in @SpringBootTest.
	 */
	@get:AliasFor(annotation = SpringBootTest::class, attribute = "classes")

	/**
	 * Optional Spring configuration classes.
	 * If not provided, Spring will try to locate configuration automatically.
	 */
	val classes: Array<KClass<*>> = []
)
