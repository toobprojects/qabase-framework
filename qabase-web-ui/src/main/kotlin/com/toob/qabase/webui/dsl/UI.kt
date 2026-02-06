package com.toob.qabase.webui.dsl

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.LocatorAssertions
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.toob.qabase.core.AllureExtensions
import com.toob.qabase.webui.PlaywrightSession
import java.util.concurrent.atomic.AtomicReference

/**
 * Minimal fluent DSL over Playwright:
 * - Kotlin sugar: type("x") into Sel.id("y")
 * - Fluent chains (Kotlin & Java): UI.visit(...).tap(...).fill(...).see(...)
 */
object UI {

	// ---------- tiny aliases (RestExpect feel) ----------
	/** alias for tap */
	@JvmStatic fun clickCss(css: String): UI = tap(css)

	/** alias for fill */
	@JvmStatic fun typeInto(css: String, value: String): UI = fill(css, value)

	/** alias for fill(locator, value) */
	@JvmStatic fun typeInto(locator: Locator, value: String): UI = apply {
		step("Type '$value' into locator") { locator.fill(value) }
	}

	/** alias for visit */
	@JvmStatic fun go(url: String): UI = visit(url)

	/** alias for see */
	@JvmOverloads
	@JvmStatic fun shouldSee(css: String, expected: String, timeoutMs: Double = 4000.0): UI =
		see(css, expected, timeoutMs)

	/** alias for seeVisible */
	@JvmOverloads
	@JvmStatic fun shouldBeVisible(css: String, timeoutMs: Double = 4000.0): UI =
		seeVisible(css, timeoutMs)

	/** alias for shouldHaveCount */
	@JvmStatic fun count(css: String, n: Int): UI = shouldHaveCount(css, n)

	// ---------- Kotlin sugar (DSL-ish) ----------
	data class Entering(val text: String)
	@JvmStatic fun type(text: String) = Entering(text)

	/** Kotlin: type("foo") into Sel.id("username") */
	infix fun Entering.into(locator: Locator) {
		step("Type '$text' into locator") { locator.fill(text) }
	}

	/** Kotlin: Sel.css("button") clicks Unit */
	infix fun Locator.clicks(@Suppress("UNUSED_PARAMETER") unit: Unit) {
		step("Click locator") { click() }
	}

	/** Kotlin: Sel.css("#toast") shouldHaveText "Welcome" */
	infix fun Locator.shouldHaveText(expected: String) {
		step("Expect locator contains '$expected'") { assertThat(this).containsText(expected) }
	}

	// ---------- Fluent (every method returns UI for chaining) ----------
	@JvmStatic fun visit(url: String): UI = apply {
		step("Visit $url") { PlaywrightSession.page().navigate(url) }
	}

	@JvmStatic fun home(): UI = apply {
		step("Open Home") { PlaywrightSession.page().navigate("/") }
	}

	@JvmStatic fun tap(css: String): UI = apply {
		step("Click CSS $css") { Sel.css(css).click() }
	}

	@JvmStatic fun click(locator: Locator): UI = apply {
		step("Click locator") { locator.click() }
	}

	@JvmStatic fun fill(css: String, value: String): UI = apply {
		step("Type '$value' into CSS $css") { Sel.css(css).fill(value) }
	}

	@JvmOverloads
	@JvmStatic
	fun see(css: String, expected: String, timeoutMs: Double = 4000.0): UI = apply {
		step("Expect CSS $css contains '$expected'") {
			assertThat(Sel.css(css)).containsText(expected,
				LocatorAssertions.ContainsTextOptions().setTimeout(timeoutMs))
		}
	}

	@JvmOverloads
	@JvmStatic
	fun shouldSeeText(text: String, timeoutMs: Double = 4000.0): UI = apply {
		step("Expect text visible in body: $text") {
			assertThat(Sel.xpath("//body//*[contains(normalize-space(.), '$text')]"))
				.isVisible(LocatorAssertions.IsVisibleOptions().setTimeout(timeoutMs))
		}
	}

	@JvmOverloads
	@JvmStatic
	fun shouldSeeTextIgnoreCase(text: String, timeoutMs: Double = 4000.0): UI = apply {
		val t = text.lowercase()
		step("Expect text visible (ignore case): $text") {
			assertThat(Sel.xpath("//*[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '$t')]"))
				.isVisible(LocatorAssertions.IsVisibleOptions().setTimeout(timeoutMs))
		}
	}

	@JvmOverloads
	@JvmStatic
	fun seeVisible(css: String, timeoutMs: Double = 4000.0): UI = apply {
		step("Expect CSS $css visible") {
			assertThat(Sel.css(css))
				.isVisible(LocatorAssertions.IsVisibleOptions().setTimeout(timeoutMs))
		}
	}

	@JvmStatic fun shouldHaveCount(css: String, count: Int): UI = apply {
		step("Expect CSS $css count = $count") {
			check(Sel.all(css).count() == count) { "Expected count=$count for '$css'" }
		}
	}

	@JvmStatic fun shouldHaveCountGreaterThan(css: String, threshold: Int): UI = apply {
		step("Expect CSS $css count > $threshold") {
			check(Sel.all(css).count() > threshold) { "Expected count>$threshold for '$css'" }
		}
	}

	@JvmStatic fun shouldHaveCountAtMost(css: String, max: Int): UI = apply {
		step("Expect CSS $css count <= $max") {
			check(Sel.all(css).count() <= max) { "Expected count<=$max for '$css'" }
		}
	}

	@JvmStatic fun shouldHaveCountLessThan(css: String, threshold: Int): UI = apply {
		step("Expect CSS $css count < $threshold") {
			check(Sel.all(css).count() < threshold) { "Expected count<$threshold for '$css'" }
		}
	}

	@JvmStatic fun shouldHaveCountAtLeast(css: String, min: Int): UI = apply {
		step("Expect CSS $css count >= $min") {
			check(Sel.all(css).count() >= min) { "Expected count>=$min for '$css'" }
		}
	}

	@JvmStatic
	fun expectAlertContains(expected: String): UI = apply {
		step("Expect alert contains '$expected'") {
			val timeoutMs = 5000L
			val pollMs = 50L
			val deadline = System.currentTimeMillis() + timeoutMs

			var matchedMessage: String? = PlaywrightSession.consumeFirstMatchingDialogMessage(expected)
			while (matchedMessage == null && System.currentTimeMillis() < deadline) {
				Thread.sleep(pollMs)
				matchedMessage = PlaywrightSession.consumeFirstMatchingDialogMessage(expected)
			}

			require(!matchedMessage.isNullOrBlank()) {
				val seen = PlaywrightSession.dialogMessagesSnapshot()
				"Expected browser alert containing <$expected>, but none was captured within ${timeoutMs}ms. Seen dialogs=$seen"
			}
		}
	}

	@JvmOverloads
	@JvmStatic
	fun clear(css: String, timeoutMs: Double = 4000.0): UI = apply {
		step("Clear CSS $css") {
			val locator = Sel.css(css)
			assertThat(locator)
				.isVisible(LocatorAssertions.IsVisibleOptions().setTimeout(timeoutMs))
			locator.clear()
		}
	}

	@JvmOverloads
	@JvmStatic
	fun clear(locator: Locator, timeoutMs: Double = 4000.0): UI = apply {
		step("Clear locator") {
			assertThat(locator)
				.isVisible(LocatorAssertions.IsVisibleOptions().setTimeout(timeoutMs))
			locator.clear()
		}
	}

	@JvmStatic
	fun scrollIntoView(css: String): UI = apply {
		step("Scroll into view: $css") { Sel.css(css).scrollIntoViewIfNeeded() }
	}

	@JvmStatic
	fun scrollIntoView(locator: Locator): UI = apply {
		step("Scroll locator into view") { locator.scrollIntoViewIfNeeded() }
	}

	@JvmStatic
	fun clickCssJs(css: String): UI = apply {
		step("Click via JS: $css") {
			val locator = Sel.css(css)
			locator.evaluate("el => el.click()")
		}
	}

	@JvmOverloads
	@JvmStatic
	fun clickCssExpectingAlertContains(css: String, expected: String, timeoutMs: Double = 5000.0): UI = apply {
		step("Click CSS $css and expect alert contains '$expected'") {
			val page = PlaywrightSession.page()
			val captured = AtomicReference<String?>()

			page.onceDialog { dialog ->
				val message = dialog.message()
				captured.set(message)
				PlaywrightSession.pushDialogMessage(message)
				runCatching { dialog.accept() }
			}

			Sel.css(css).click()
			page.waitForCondition(
				{ captured.get() != null },
				Page.WaitForConditionOptions().setTimeout(timeoutMs)
			)

			val message = captured.get()
			require(!message.isNullOrBlank()) { "Expected browser alert, but none was captured after clicking '$css'" }
			require(message.contains(expected, ignoreCase = true)) {
				"Alert text <$message> did not contain <$expected>"
			}
		}
	}

	private inline fun <T> step(name: String, crossinline body: () -> T): T =
		AllureExtensions.step(name) { body() }
}
