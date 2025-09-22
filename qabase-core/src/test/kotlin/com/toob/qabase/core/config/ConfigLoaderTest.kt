package com.toob.qabase.core.config

import io.github.oshai.kotlinlogging.KotlinLogging
import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithName
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private val log = KotlinLogging.logger {}

/**
 * Verifies that ConfigLoader.loadMapping can produce a typed mapping from default sources
 * (system properties / env) and that @WithName handles kebab-case keys.
 */
class ConfigLoaderTest {

	@AfterEach
	fun tearDown() {
		// clean up any system properties we set so tests are isolated
		listOf(
			"qabase.webui.base-url",
			"qabase.webui.browser",
			"qabase.webui.browser-window-size",
			"qabase.webui.timeout",
			"qabase.webui.headless"
		).forEach(System::clearProperty)
	}

	@Test
	fun `should load Web UI mapping from system properties`() {
		// Arrange: provide values via system properties (picked up by SmallRye default sources)
		System.setProperty("qabase.webui.base-url", "https://www.demoblaze.com")
		System.setProperty("qabase.webui.browser", "chrome")
		System.setProperty("qabase.webui.browser-window-size", "1920x1080")
		System.setProperty("qabase.webui.timeout", "10000")
		System.setProperty("qabase.webui.headless", "true")

		// Act
		val cfg = ConfigLoader.loadMapping(WebUiConfig::class)

		// Assert
		log.info { "Loaded Web UI config: baseUrl=${cfg.baseUrl()}, browser=${cfg.browser()}, size=${cfg.browserWindowSize()}, timeout=${cfg.timeout()}, headless=${cfg.headless()}" }

		assertEquals("https://www.demoblaze.com", cfg.baseUrl())
		assertEquals("chrome", cfg.browser())
		assertEquals("1920x1080", cfg.browserWindowSize())
		assertEquals(10_000L, cfg.timeout())
		assertEquals(true, cfg.headless())
	}

	/**
	 * Mapping interface mirrors:
	 *
	 * qabase:
	 *   webui:
	 *     base-url: ...
	 *     browser: ...
	 *     browser-window-size: ...
	 *     timeout: ...
	 *     headless: ...
	 */
	@ConfigMapping(prefix = "qabase.webui")
	interface WebUiConfig {
		@WithName("base-url") fun baseUrl(): String
		fun browser(): String
		@WithName("browser-window-size") fun browserWindowSize(): String
		fun timeout(): Long
		fun headless(): Boolean
	}
}