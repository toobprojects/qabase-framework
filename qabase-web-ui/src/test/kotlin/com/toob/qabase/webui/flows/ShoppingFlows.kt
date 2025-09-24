package com.toob.qabase.webui.flows

import com.toob.qabase.webui.QaWebUiTest
import com.toob.qabase.webui.dsl.UI
import com.toob.qabase.webui.pages.HomePage
import com.toob.qabase.webui.pages.ProductPage

class ShoppingFlows {

	private val homePage = HomePage()
	private val productPage = ProductPage()

	fun addSamsungS6ToCart(): ShoppingFlows {
		homePage
			.open()
			.verifyVisible()
			.shouldSeeProducts()
			.openProductById(1)

		productPage
			.verifyVisible()
			.shouldShowTitle("Samsung")
			.addToCart()

		// DemoBlaze shows a JS alert, not a modal
		UI.expectAlertContains("Product added")

		// Go to Cart page (navbar link)
		UI.clickCss("#cartur")

		// Wait for the cart table and validate item name
		UI.shouldBeVisible("#tbodyid")
			.shouldSee("#tbodyid", "Samsung")

		return this
	}
}