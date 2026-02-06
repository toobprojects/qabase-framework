package com.toob.qabase.webui

import com.toob.qabase.core.config.ConfigLoader
import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault
import io.smallrye.config.WithName

/**
 * Type-safe, DI-free config mapping for the "qabase.webui" prefix.
 * SmallRye generates an implementation at runtime; you just call the getters.
 */
@ConfigMapping(prefix = "qabase.webui")
interface WebUiConfig {
	/** Base URL for the web UI under test */
	@WithName("base-url")
	@WithDefault("http://localhost:8080")
	fun baseUrl(): String

	/** Browser engine type: chromium, firefox, webkit */
	@WithName("browser-type")
	@WithDefault("chromium")
	fun browserType(): String

	/** Viewport width in pixels */
	@WithName("viewport-width")
	@WithDefault("1366")
	fun viewportWidth(): Int

	/** Viewport height in pixels */
	@WithName("viewport-height")
	@WithDefault("768")
	fun viewportHeight(): Int

	/** Timeout in milliseconds */
	@WithName("timeout-ms")
	@WithDefault("10000")
	fun timeoutMs(): Double

	/** Run browser in headless mode */
	@WithDefault("true")
	fun headless(): Boolean

	/** Record a Playwright trace and attach it to Allure when a test fails */
	@WithName("trace-on-failure")
	@WithDefault("true")
	fun traceOnFailure(): Boolean
}

fun loadWebUiConfig(): WebUiConfig =
	ConfigLoader.loadMapping(WebUiConfig::class)
