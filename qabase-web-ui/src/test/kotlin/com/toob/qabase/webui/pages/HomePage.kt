//package com.toob.qabase.webui.pages
//
//import com.toob.qabase.webui.dsl.UI
//import io.qameta.allure.Step
//import org.springframework.stereotype.Component
//
///** DemoBlaze Home page (example app). */
//@Component
//class HomePage : VisiblePage<HomePage> {
//
//	@Step("Open Home")
//	fun open(): HomePage = apply { UI.home() }
//
//	@Step("Ensure product grid is visible")
//	fun shouldSeeProducts(): HomePage {
//		UI.shouldBeVisible("#tbodyid")
//		return this
//	}
//
//	@Step("Open product by id={id}")
//	fun openProductById(id: Int): ProductPage {
//		UI.clickCss("a[href='prod.html?idp_=$id']")
//		return ProductPage()
//	}
//
//	@Step("Verify Home page visible")
//	override fun verifyVisible(): HomePage {
//		// Navbar is visible and stable
//		UI.shouldBeVisible("#navbarExample")
//		// Products area exists (sometimes lazy-visible depending on viewport)
//		UI.shouldBeVisible("#tbodyid")
//		return this
//	}
//}