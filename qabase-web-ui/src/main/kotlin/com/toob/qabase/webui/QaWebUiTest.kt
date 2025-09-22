package com.toob.qabase.webui

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

/**
 * SpringBootTest-like meta-annotation for REST tests.
 * Applies RestAssuredExtension to configure REST Assured from application.yml (SmallRye).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@ExtendWith(SelenideExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
annotation class QaWebUiTest()
