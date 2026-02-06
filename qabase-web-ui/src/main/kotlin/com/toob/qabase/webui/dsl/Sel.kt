package com.toob.qabase.webui.dsl

import com.microsoft.playwright.Locator
import com.toob.qabase.webui.PlaywrightSession

/** Ultra-lean selector helpers. Short, readable, Java/Kotlin-friendly. */
object Sel {

	/** Go to base URL ("/") configured by Playwright context. */
	@JvmStatic fun open() {
		PlaywrightSession.page().navigate("/")
	}

	/** Find by name attribute. */
	@JvmStatic fun name(name: String): Locator = PlaywrightSession.page().locator("[name='$name']")

	/** Find by id attribute. */
	@JvmStatic fun id(id: String): Locator = PlaywrightSession.page().locator("#$id")

	/** Find by CSS selector. */
	@JvmStatic fun css(selector: String): Locator = PlaywrightSession.page().locator(selector)

	/** Find a collection by CSS selector. */
	@JvmStatic fun all(selector: String): Locator = PlaywrightSession.page().locator(selector)

	/** Element found inside a scope (CSS within CSS). */
	@JvmStatic fun inScope(scopeCss: String, childCss: String): Locator =
		css(scopeCss).locator(childCss)

	/** Elements found inside a scope (CSS within CSS). */
	@JvmStatic fun inAll(scopeCss: String, childCss: String): Locator =
		css(scopeCss).locator(childCss)

	/** Find by XPath expression. */
	@JvmStatic fun xpath(expression: String): Locator = PlaywrightSession.page().locator("xpath=$expression")
}
