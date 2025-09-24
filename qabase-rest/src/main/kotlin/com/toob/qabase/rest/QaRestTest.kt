package com.toob.qabase.rest

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

/**
 * SpringBootTest-like meta-annotation for REST tests.
 * Applies RestAssuredExtension to configure REST Assured from application.yml (SmallRye).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@ExtendWith(RestAssuredExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
annotation class QaRestTest