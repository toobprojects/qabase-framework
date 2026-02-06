package com.toob.qabase.webui

import com.toob.qabase.core.util.logger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class WebUiConfigTest {

	private val log = logger()

	@Test
	@DisplayName("loadWebUiConfig() reads application.yaml (qabase.webui) and maps fields correctly")
	fun loadsYamlAndMaps() {
		val cfg = loadWebUiConfig()

		log.info {
			"🌐 Loaded WebUI config: baseUrl='${cfg.baseUrl()}', browserType='${cfg.browserType()}', " +
					"viewport='${cfg.viewportWidth()}x${cfg.viewportHeight()}', timeout=${cfg.timeoutMs()}, " +
					"headless=${cfg.headless()}, traceOnFailure=${cfg.traceOnFailure()} ✅"
		}

		assertTrue(cfg.baseUrl().isNotBlank(), "baseUrl should not be blank")
		assertTrue(cfg.browserType().isNotBlank(), "browserType should not be blank")
		assertTrue(cfg.viewportWidth() > 0, "viewportWidth should be > 0")
		assertTrue(cfg.viewportHeight() > 0, "viewportHeight should be > 0")
		assertTrue(cfg.timeoutMs() > 0, "timeoutMs should be > 0")

		assertEquals("https://www.demoblaze.com", cfg.baseUrl())
		assertEquals("chromium", cfg.browserType())
		assertEquals(1920, cfg.viewportWidth())
		assertEquals(1080, cfg.viewportHeight())
		assertEquals(10_000.0, cfg.timeoutMs())
		assertTrue(cfg.headless())
		assertTrue(cfg.traceOnFailure())
	}

	@Test
	@DisplayName("loadWebUiConfig() can be called multiple times (idempotent load)")
	fun idempotentLoads() {
		val a = loadWebUiConfig()
		val b = loadWebUiConfig()

		log.info { "🔁 Comparing two loads: a.baseUrl='${a.baseUrl()}' ↔ b.baseUrl='${b.baseUrl()}' 🧪" }
		assertEquals(a.baseUrl(), b.baseUrl())
		assertEquals(a.browserType(), b.browserType())
		assertEquals(a.viewportWidth(), b.viewportWidth())
		assertEquals(a.viewportHeight(), b.viewportHeight())
		assertEquals(a.timeoutMs(), b.timeoutMs())
		assertEquals(a.headless(), b.headless())
		assertEquals(a.traceOnFailure(), b.traceOnFailure())
	}
}
