package com.toob.qabase.webui.test

import com.toob.qabase.QaBaseTest
import com.toob.qabase.webui.pages.HomePage
import com.toob.qabase.webui.pages.PageFactory
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test

@QaBaseTest
class DemoBlazeKotlinPomTest(
	@Autowired private val pages: PageFactory) {


	@Test
	@DisplayName("🛒 POM (Kotlin): Add Samsung S6 to cart")
	fun addSamsungToCart() {
		pages.get(HomePage::class)
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