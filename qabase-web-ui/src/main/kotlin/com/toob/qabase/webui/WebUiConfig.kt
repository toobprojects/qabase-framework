package com.toob.qabase.webui

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault
import org.eclipse.microprofile.config.ConfigProvider
import io.smallrye.config.SmallRyeConfig
import io.smallrye.config.SmallRyeConfigBuilder
import java.util.NoSuchElementException

/**
 * Type-safe, DI-free config mapping for the "qabase.webui" prefix.
 * SmallRye generates an implementation at runtime; you just call the getters.
 *
 * Example YAML:
 * qabase:
 *   webui:
 *     base-url: "https://www.demoblaze.com"
 *     browser: "chrome"
 *     browser-window-size: "1920x1080"
 *     timeout: 10000
 *     headless: true
 */
@ConfigMapping(prefix = "qabase.webui")
interface WebUiConfig {
	/** Base URL for the web UI under test */
	@WithDefault("http://localhost:8080")
	fun baseUrl(): String

	/** Browser to use (chrome, firefox, etc.) */
	@WithDefault("chrome")
	fun browser(): String

	/** Browser window size (e.g. 1920x1080) */
	@WithDefault("1366x768")
	fun browserWindowSize(): String

	/** Timeout in milliseconds */
	@WithDefault("6000")
	fun timeout(): Long

	/** Run browser in headless mode */
	@WithDefault("false")
	fun headless(): Boolean
}

/**
 * Loads the WebUiConfig mapping.
 *
 * 1. Try to use the current ConfigProvider (what a runtime would normally give us).
 * 2. If the mapping is not found there, fall back to creating our own SmallRyeConfig
 *    with default + discovered sources so that application.yaml is read.
 */
fun loadWebUiConfig(): WebUiConfig {
	val currentConfig = ConfigProvider.getConfig()

	// Case 1: ConfigProvider already gives us a SmallRyeConfig
	if (currentConfig is SmallRyeConfig) {
		return runCatching {
			currentConfig.getConfigMapping(WebUiConfig::class.java)
		}.getOrElse { exception ->
			if (exception is NoSuchElementException) {
				bootstrapWebUiConfig().getConfigMapping(WebUiConfig::class.java)
			} else {
				throw exception
			}
		}
	}

	// Case 2: ConfigProvider is not SmallRyeConfig -> always bootstrap
	return bootstrapWebUiConfig().getConfigMapping(WebUiConfig::class.java)
}

private fun bootstrapWebUiConfig(): SmallRyeConfig =
	SmallRyeConfigBuilder()
		.withMapping(WebUiConfig::class.java)
		.addDefaultSources()
		.addDiscoveredSources()
		.addDiscoveredConverters()
		.build()