package com.toob.qabase.webui

import com.toob.qabase.QaBaseTest
import com.toob.qabase.webui.dsl.UI
import com.toob.qabase.webui.pages.HomePage
import com.toob.qabase.webui.pages.PageFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test

@QaBaseTest
class ProductJourneyTestIT(@Autowired private val pages: PageFactory) {

	@Test
	fun openAndAdd() {
		pages.get(HomePage::class)
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