package com.toob.qabase.webui

import com.toob.qabase.webui.dsl.UI
import com.toob.qabase.webui.pages.HomePage
import kotlin.test.Test

@QaWebUiTest
class ProductJourneyTestIT {

	private val homePage = HomePage()

	@Test
	fun openAndAdd() {
		homePage
			.open()
			.verifyVisible()
			.shouldSeeProducts()
			.openProductById(1)
			.verifyVisible()
			.shouldShowTitle("Samsung")
			.addToCart()
		UI.expectAlertContains("Product added")
	}
}