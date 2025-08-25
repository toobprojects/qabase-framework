package com.toob.qabase.webui.pages

import com.toob.qabase.webui.dsl.UI
import io.qameta.allure.Step
import org.springframework.stereotype.Component

/** Product details page */
@Component
class ProductPage : VisiblePage<ProductPage> {

	@Step("Verify Product page visible")
	override fun verifyVisible(): ProductPage {
		UI.shouldBeVisible("#tbodyid")
		return this
	}

	@Step("Add product to cart")
	fun addToCart(): ProductPage {
		UI.clickCss("a[onclick^='addToCart']")
		return this
	}

	@Step("Confirm 'Product added' alert")
	fun confirmAdded(): ProductPage {
		UI.expectAlertContains("Product added")
		return this
	}

	@Step("Should show product title contains: {text}")
	fun shouldShowTitle(text: String): ProductPage {
		UI.shouldSee(".name", text)
		return this
	}
}