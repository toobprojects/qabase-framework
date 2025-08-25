package com.toob.qabase.webui.flows

import com.toob.qabase.webui.dsl.UI
import com.toob.qabase.webui.pages.HomePage
import com.toob.qabase.webui.pages.PageFactory
import com.toob.qabase.webui.pages.ProductPage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ShoppingFlows(private val pages: PageFactory) {

	fun addSamsungS6ToCart(): ShoppingFlows {
		pages.get(HomePage::class.java)
			.open()
			.verifyVisible()
			.shouldSeeProducts()
			.openProductById(1)

		pages.get(ProductPage::class.java)
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