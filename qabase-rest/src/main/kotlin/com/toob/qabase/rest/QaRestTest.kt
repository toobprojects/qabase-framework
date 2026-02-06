package com.toob.qabase.rest

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.annotation.Inherited

/**
 * SpringBootTest-like meta-annotation for REST tests.
 * Applies RestAssuredExtension to configure REST Assured from application.yml (SmallRye).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@ExtendWith(RestAssuredExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
annotation class QaRestTest