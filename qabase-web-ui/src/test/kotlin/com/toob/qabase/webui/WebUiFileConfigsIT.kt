package com.toob.qabase.webui

import com.toob.qabase.QaBaseTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@QaBaseTest
class WebUiFileConfigsIT {

	@Autowired
	lateinit var webUiFileConfig: WebUIConfigs

	@Test
	fun `Should Load Web UI & Selenide Configurations`() {
		assertNotNull(webUiFileConfig)

		assertEquals("https://www.demoblaze.com", webUiFileConfig.baseUrl)
		assertEquals("chrome", webUiFileConfig.browser)
		assertEquals("1920x1080", webUiFileConfig.browseWindowSize)
		assertEquals(10000, webUiFileConfig.timeout)
		assertTrue(webUiFileConfig.headless)
	}
}