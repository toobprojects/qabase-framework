package com.toob.qabase.webui

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.logevents.LogEventListener
import com.codeborne.selenide.logevents.SelenideLogger
import com.toob.qabase.core.AllureExtensions
import com.toob.qabase.core.support.OSSupport
import io.qameta.allure.selenide.AllureSelenide
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * JUnit 5 extension (no Spring) that configures Selenide and Allure from MicroProfile config.
 */
class SelenideExtension : BeforeAllCallback, AfterAllCallback {

	override fun beforeAll(context: ExtensionContext) {
		val cfg = loadWebUiConfig()

		// Validate & apply Selenide configuration
		OSSupport.validateBrowserOnOS(cfg.browser())
		Configuration.baseUrl = cfg.baseUrl()
		Configuration.browser = cfg.browser()
		Configuration.timeout = cfg.timeout()
		Configuration.browserSize = cfg.browserWindowSize()
		Configuration.headless = cfg.headless()

		// Enable Allure listener if Allure is enabled
		registerAllureSelenide()
	}

	override fun afterAll(context: ExtensionContext) {
		// Close browser after test class
		runCatching { Selenide.closeWebDriver() }
		// Remove Allure listener
		runCatching<LogEventListener?> { SelenideLogger.removeListener("AllureSelenide") }
	}

	private fun registerAllureSelenide() {
		if (!AllureExtensions.allureEnabled()) return
		runCatching<LogEventListener?> { SelenideLogger.removeListener("AllureSelenide") }
		SelenideLogger.addListener(
			"AllureSelenide",
			AllureSelenide()
				.screenshots(true)
				.savePageSource(true)
				.includeSelenideSteps(true)
		)
	}
}