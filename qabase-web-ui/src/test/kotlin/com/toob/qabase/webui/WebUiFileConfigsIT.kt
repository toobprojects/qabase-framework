package com.toob.qabase.webui

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import com.toob.qabase.core.util.logger

class WebUiConfigTest {

	private val log = logger()

	@Test
	@DisplayName("loadWebUiConfig() reads application.yaml (qabase.webui) and maps fields correctly")
	fun loadsYamlAndMaps() {
		val cfg = loadWebUiConfig()

		// Basic sanity: no null/blank critical fields
		log.info {
			"🌐 Loaded WebUI config: baseUrl='${cfg.baseUrl()}', browser='${cfg.browser()}', " +
					"window='${cfg.browserWindowSize()}', timeout=${cfg.timeout()}, headless=${cfg.headless()} ✅"
		}

		assertTrue(cfg.baseUrl().isNotBlank(), "baseUrl should not be blank")
		assertTrue(cfg.browser().isNotBlank(), "browser should not be blank")
		assertTrue(cfg.browserWindowSize().isNotBlank(), "browserWindowSize should not be blank")
		assertTrue(cfg.timeout() > 0, "timeout should be > 0")

		// If the repo ships a default demo config (DemoBlaze), validate those exact values
		// Adjust these expectations if you change src/main/resources/application.yaml
		assertEquals("https://www.demoblaze.com", cfg.baseUrl())
		assertEquals("chrome", cfg.browser())
		assertEquals("1920x1080", cfg.browserWindowSize())
		assertEquals(10_000, cfg.timeout())
		assertTrue(cfg.headless())
	}

	@Test
	@DisplayName("loadWebUiConfig() can be called multiple times (idempotent load)")
	fun idempotentLoads() {
		val a = loadWebUiConfig()
		val b = loadWebUiConfig()
		log.info { "🔁 Comparing two loads: a.baseUrl='${a.baseUrl()}' ↔ b.baseUrl='${b.baseUrl()}' 🧪" }
		assertEquals(a.baseUrl(), b.baseUrl())
		assertEquals(a.browser(), b.browser())
		assertEquals(a.browserWindowSize(), b.browserWindowSize())
		assertEquals(a.timeout(), b.timeout())
		assertEquals(a.headless(), b.headless())
	}
}