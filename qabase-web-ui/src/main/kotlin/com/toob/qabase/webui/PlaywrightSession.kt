package com.toob.qabase.webui

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright

object PlaywrightSession {
	private val pwTl = ThreadLocal<Playwright>()
	private val browserTl = ThreadLocal<Browser>()
	private val contextTl = ThreadLocal<BrowserContext>()
	private val pageTl = ThreadLocal<Page>()
	private val dialogsTl = ThreadLocal.withInitial { mutableListOf<String>() }

	fun setPlaywright(playwright: Playwright) = pwTl.set(playwright)
	fun setBrowser(browser: Browser) = browserTl.set(browser)
	fun setContext(context: BrowserContext) = contextTl.set(context)
	fun setPage(page: Page) = pageTl.set(page)

	fun playwright(): Playwright = requireNotNull(pwTl.get()) { "Playwright is not initialized" }
	fun browser(): Browser = requireNotNull(browserTl.get()) { "Browser is not initialized" }
	fun context(): BrowserContext = requireNotNull(contextTl.get()) { "BrowserContext is not initialized" }
	fun page(): Page = requireNotNull(pageTl.get()) { "Page is not initialized" }

	fun pushDialogMessage(message: String) {
		dialogsTl.get().add(message)
	}

	fun lastDialogMessage(): String? = dialogsTl.get().lastOrNull()

	fun consumeFirstMatchingDialogMessage(expected: String): String? {
		val dialogs = dialogsTl.get()
		val idx = dialogs.indexOfFirst { it.contains(expected, ignoreCase = true) }
		return if (idx >= 0) dialogs.removeAt(idx) else null
	}

	fun dialogMessagesSnapshot(): List<String> = dialogsTl.get().toList()

	fun clearPerTest() {
		dialogsTl.get().clear()
		pageTl.remove()
		contextTl.remove()
	}

	fun clearAll() {
		clearPerTest()
		browserTl.remove()
		pwTl.remove()
	}
}
