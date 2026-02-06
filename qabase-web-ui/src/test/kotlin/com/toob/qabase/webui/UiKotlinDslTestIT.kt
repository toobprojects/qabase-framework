package com.toob.qabase.webui

import com.toob.qabase.webui.dsl.Sel
import com.toob.qabase.webui.dsl.UI
import com.toob.qabase.webui.dsl.UI.into
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

@QaWebUiTest
class UiKotlinDslTestIT {

	@Test
	@DisplayName("🔎 Kotlin DSL: infix + fluent")
	fun infixAndFluent() {
		UI.visit("https://www.demoblaze.com/")

		// open the login modal
		UI.tap("#login2")
			.seeVisible("#logInModal")

		// fill the modal fields using the DSL sugar
		UI.type("thabo") into Sel.id("loginusername")
		UI.fill("#loginpassword", "secret")
			.tap("#logInModal button[onclick='logIn()']")

		// expect the site-level JS alert (no real account exists)
		UI.expectAlertContains("Wrong password.")
	}
}
