package com.toob.qabase.webui

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

/**
 * SpringBootTest-like meta-annotation for Playwright UI tests.
 * Applies PlaywrightExtension to configure browser/session lifecycle from application.yaml.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@ExtendWith(PlaywrightExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
annotation class QaWebUiTest
