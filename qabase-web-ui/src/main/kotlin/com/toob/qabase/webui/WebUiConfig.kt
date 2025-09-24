package com.toob.qabase.webui

import com.toob.qabase.core.config.ConfigLoader
import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault

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
fun loadWebUiConfig(): WebUiConfig =
	ConfigLoader.loadMapping(WebUiConfig::class)