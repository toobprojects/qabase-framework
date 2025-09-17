package com.toob.qabase


// Specifies this annotation can only be applied to classes (same as @Target(ElementType.TYPE) in Java).
@Target(AnnotationTarget.CLASS)

// Ensures the annotation is available at runtime, so Spring Boot can detect and process it.
@Retention(AnnotationRetention.RUNTIME)

// Ensures this annotation appears in generated documentation (same as Java’s @Documented).
@MustBeDocumented

annotation class QaBaseAutomationModule()
