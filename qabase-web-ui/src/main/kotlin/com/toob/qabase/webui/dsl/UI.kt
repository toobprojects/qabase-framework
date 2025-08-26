package com.toob.qabase.webui.dsl

import com.codeborne.selenide.Condition.*
import com.codeborne.selenide.CollectionCondition.size
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Screenshots
import com.codeborne.selenide.Selenide.*
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.WebDriverRunner
import io.qameta.allure.Step
import java.time.Duration
import com.toob.qabase.core.AllureExtensions
import io.qameta.allure.Allure

/**
 * Minimal fluent DSL over Selenide:
 * - Kotlin sugar: type("x") into Sel.id("y"), Sel.css("...").clicks(Unit)
 * - Fluent chains (Kotlin & Java): UI.visit(...).clickCss(...).typeInto(...).shouldSee(...)
 * - On failure: auto-attach screenshot + page source to Allure.
 */
object UI {

	// ---------- Kotlin sugar (DSL-ish) ----------
	data class Entering(val text: String)
	@JvmStatic fun type(text: String) = Entering(text)

	/** Kotlin: type("foo") into Sel.id("username") */
	infix fun Entering.into(el: SelenideElement) =
		step("""Type "$text" into $el""") { el.shouldBe(visible).setValue(text) }

	/** Kotlin: Sel.css("button").clicks(Unit) */
	infix fun SelenideElement.clicks(@Suppress("UNUSED_PARAMETER") unit: Unit) =
		step("Click $this") { click() }

	/** Kotlin: Sel.css("#toast").shouldHaveText("Welcome") */
	infix fun SelenideElement.shouldHaveText(expected: String) =
		step("""Expect "$expected" on $this""") { shouldHave(text(expected)) }

	// ---------- Fluent (every method returns UI for chaining) ----------
	@Step("Visit {url}")
	@JvmStatic fun visit(url: String): UI = apply { open(url) }

	@Step("Home")
	@JvmStatic fun home(): UI = apply { open("/") }

	@Step("Click CSS {css}")
	@JvmStatic fun clickCss(css: String): UI = apply { Sel.css(css).shouldBe(enabled).click() }

	@Step("Click element")
	@JvmStatic fun click(el: SelenideElement): UI = apply { el.shouldBe(enabled).click() }

	@Step("Type '{value}' into CSS {css}")
	@JvmStatic fun typeInto(css: String, value: String): UI =
		apply { Sel.css(css).shouldBe(visible).setValue(value) }

	@Step("Type '{value}' into element")
	@JvmStatic fun typeInto(el: SelenideElement, value: String): UI =
		apply { el.shouldBe(visible).setValue(value) }

	@JvmOverloads
	@Step("Expect CSS {css} contains '{expected}' (timeoutMs={timeoutMs})")
	@JvmStatic fun shouldSee(css: String, expected: String, timeoutMs: Long = Configuration.timeout): UI =
		apply { Sel.css(css).shouldHave(text(expected), Duration.ofMillis(timeoutMs)) }

	@JvmOverloads
	@Step("Expect CSS {css} is visible (timeoutMs={timeoutMs})")
	@JvmStatic fun shouldBeVisible(css: String, timeoutMs: Long = Configuration.timeout): UI =
		apply { Sel.css(css).shouldBe(visible, Duration.ofMillis(timeoutMs)) }

	@Step("Expect CSS {css} count = {count}")
	@JvmStatic fun shouldHaveCount(css: String, count: Int): UI =
		apply { Sel.all(css).shouldHave(size(count)) }

	@Step("Expect alert contains '{expected}' and accept")
	@JvmStatic fun expectAlertContains(expected: String): UI = apply {
		val a = switchTo().alert()
		require(a.text.contains(expected, true)) {
			"Alert text <${a.text}> did not contain <$expected>"
		}
		a.accept()
	}

	// ---------- tiny aliases (RestExpect feel) ----------
	/** alias for clickCss */
	@JvmStatic fun tap(css: String): UI = clickCss(css)

	/** alias for typeInto */
	@JvmStatic fun fill(css: String, value: String): UI = typeInto(css, value)

	/** alias for visit */
	@JvmStatic fun go(url: String): UI = visit(url)

	/** alias for shouldSee */
	@JvmOverloads
	@JvmStatic fun see(css: String, expected: String, timeoutMs: Long = 4000): UI =
		shouldSee(css, expected, timeoutMs)

	/** alias for shouldBeVisible */
	@JvmOverloads
	@JvmStatic fun seeVisible(css: String, timeoutMs: Long = 4000): UI =
		shouldBeVisible(css, timeoutMs)

	/** alias for shouldHaveCount */
	@JvmStatic fun count(css: String, n: Int): UI = shouldHaveCount(css, n)


	// ----- tiny step wrapper with failure artifacts -----
	private inline fun <T> step(name: String, crossinline body: () -> T): T {
		return try {
			AllureExtensions.step(name) { body() }
		} catch (t: Throwable) {
			// Take Screenot and attach Page Source on failure
			attachFailureArtifacts()
			throw t
		}
	}

	// ----- failure artifacts helper -----
	private fun attachFailureArtifacts() {

		// Capture the page source
		runCatching {
			Screenshots.takeScreenShotAsFile()
				?.inputStream()?.use {
					Allure.addAttachment("Screenshot", "image/png", it, "png")
				}
		}

		// Capture the page source
		runCatching {
			val html = WebDriverRunner.getWebDriver().pageSource
			Allure.addAttachment("Page Source", "text/html", html, ".html")
		}
	}
}