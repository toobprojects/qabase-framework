package com.toob.qabase.webui.dsl

import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.`$$`
import com.codeborne.selenide.Selenide.`$x`
import com.codeborne.selenide.Selenide.open
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ElementsCollection
import org.openqa.selenium.By

/** Ultra-lean selector helpers. Short, readable, Java/Kotlin-friendly. */
object Sel {

	/** Go to base URL ("/") configured by Selenide. */
	@JvmStatic fun open() = open("/")

	/** Find by name attribute. */
	@JvmStatic fun name(name: String): SelenideElement = `$`(By.name(name))

	/** Find by id attribute. */
	@JvmStatic fun id(id: String): SelenideElement = `$`(By.id(id))

	/** Find by CSS selector. */
	@JvmStatic fun css(selector: String): SelenideElement = `$`(selector)

	/** Find a collection by CSS selector. */
	@JvmStatic fun all(selector: String): ElementsCollection = `$$`(selector)

	// --- optional tiny scope helpers (no new classes) ---
	/** Element found inside a scope (CSS within CSS). */
	@JvmStatic fun inScope(scopeCss: String, childCss: String): SelenideElement =
		css(scopeCss).find(childCss)

	/** Elements found inside a scope (CSS within CSS). */
	@JvmStatic fun inAll(scopeCss: String, childCss: String): ElementsCollection =
		css(scopeCss).findAll(childCss)

    /** Find by XPath expression. */
    @JvmStatic fun xpath(expression: String): SelenideElement = `$x`(expression)
}