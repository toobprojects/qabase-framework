package com.toob.qabase.webui

import com.toob.qabase.webui.pages.HomePage
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

@QaWebUiTest
class DemoBlazeKotlinPomTestIT() {

	private val homePage = HomePage()

	@Test
	@DisplayName("🛒 POM (Kotlin): Add Samsung S6 to cart")
	fun addSamsungToCart() {
		homePage
			.open()
			.verifyVisible()
			.shouldSeeProducts()
			.openProductById(1)
			.verifyVisible()
			.shouldShowTitle("Samsung")
			.addToCart()
			.confirmAdded()
	}
}