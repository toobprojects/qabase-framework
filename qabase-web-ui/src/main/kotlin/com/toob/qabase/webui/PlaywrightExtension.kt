package com.toob.qabase.webui

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.Tracing
import io.qameta.allure.Allure
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.nio.file.Files

/**
 * JUnit 5 extension (no Spring) that configures Playwright from MicroProfile config.
 */
class PlaywrightExtension : BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

	override fun beforeAll(context: ExtensionContext) {
		val cfg = loadWebUiConfig()
		val playwright = Playwright.create()
		val browser = launchBrowser(playwright, cfg)

		PlaywrightSession.setPlaywright(playwright)
		PlaywrightSession.setBrowser(browser)
	}

	override fun beforeEach(context: ExtensionContext) {
		val cfg = loadWebUiConfig()
		val browserContext = PlaywrightSession.browser().newContext(
			Browser.NewContextOptions()
				.setBaseURL(cfg.baseUrl())
				.setViewportSize(cfg.viewportWidth(), cfg.viewportHeight())
		)

		if (cfg.traceOnFailure()) {
			browserContext.tracing().start(
				Tracing.StartOptions()
					.setScreenshots(true)
					.setSnapshots(true)
					.setSources(true)
			)
		}

		val page = browserContext.newPage()
		page.setDefaultTimeout(cfg.timeoutMs())
		page.setDefaultNavigationTimeout(cfg.timeoutMs())

		// Auto-accept dialogs so UI flow methods remain fluent and non-blocking.
		page.onDialog { dialog ->
			PlaywrightSession.pushDialogMessage(dialog.message())
			runCatching { dialog.accept() }
		}

		PlaywrightSession.setContext(browserContext)
		PlaywrightSession.setPage(page)
	}

	override fun afterEach(context: ExtensionContext) {
		val cfg = loadWebUiConfig()
		val failed = context.executionException.isPresent
		val page = runCatching { PlaywrightSession.page() }.getOrNull()
		val browserContext = runCatching { PlaywrightSession.context() }.getOrNull()

		if (failed && page != null) {
			attachFailureArtifacts(page)
		}

		if (browserContext != null) {
			runCatching {
				if (cfg.traceOnFailure() && failed) {
					val tracePath = Files.createTempFile("playwright-trace-", ".zip")
					browserContext.tracing().stop(
						Tracing.StopOptions().setPath(tracePath)
					)
					Files.newInputStream(tracePath).use {
						Allure.addAttachment("Playwright Trace", "application/zip", it, "zip")
					}
				} else if (cfg.traceOnFailure()) {
					browserContext.tracing().stop()
				}
			}
			runCatching { browserContext.close() }
		}

		PlaywrightSession.clearPerTest()
	}

	override fun afterAll(context: ExtensionContext) {
		runCatching { PlaywrightSession.browser().close() }
		runCatching { PlaywrightSession.playwright().close() }
		PlaywrightSession.clearAll()
	}

	private fun launchBrowser(playwright: Playwright, cfg: WebUiConfig): Browser {
		val options = BrowserType.LaunchOptions().setHeadless(cfg.headless())

		return when (cfg.browserType().lowercase()) {
			"chromium", "chrome" -> playwright.chromium().launch(options)
			"firefox" -> playwright.firefox().launch(options)
			"webkit", "safari" -> playwright.webkit().launch(options)
			else -> error("Unsupported qabase.webui.browser-type='${cfg.browserType()}'")
		}
	}

	private fun attachFailureArtifacts(page: Page) {
		runCatching {
			val png = page.screenshot(Page.ScreenshotOptions().setFullPage(true))
			Allure.addAttachment("Screenshot", "image/png", png.inputStream(), "png")
		}

		runCatching {
			Allure.addAttachment("Page Source", "text/html", page.content(), ".html")
		}
	}
}
