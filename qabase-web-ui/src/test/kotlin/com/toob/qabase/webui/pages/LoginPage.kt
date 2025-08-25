package com.toob.qabase.webui.pages

import com.toob.qabase.webui.dsl.UI
import io.qameta.allure.Step
import org.springframework.stereotype.Component

@Component
class LoginPage : VisiblePage<LoginPage> {

	@Step("Open Login")
	fun open(): LoginPage {
		UI.visit("https://example.com/login")
		return this
	}

	@Step("Type username")
	fun username(value: String): LoginPage {
		UI.fill("#username", value)
		return this
	}

	@Step("Type password")
	fun password(value: String): LoginPage {
		UI.fill("#password", value)
		return this
	}

	@Step("Submit form")
	fun submit(): LoginPage {
		UI.tap("button[type=submit]")
		return this
	}

	@Step("Should see welcome message")
	fun shouldWelcome(text: String = "Welcome"): LoginPage {
		UI.see(".message", text)
		return this
	}

	@Step("Verify Login page visible")
	override fun verifyVisible(): LoginPage {
		UI.seeVisible("form#login")
		return this
	}
}