package com.toob.qabase.webui.test

import com.toob.qabase.QaBaseTest
import com.toob.qabase.webui.page.DemoBlazePage
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test

@Epic("Demo Blaze E-Commerce")
@Feature("Adding A Product To The Cart")
@QaBaseTest
class DemoBlazeIT {

	@Autowired
	lateinit var demoBlazePage : DemoBlazePage

	@Test
	@Story("Verify That A Product Can Be Added To Cart And Alert Is Confirmed")
	fun `Add A Device To The Cart`() {
		demoBlazePage
			.openPage()
			.selectProduct(1)
			.addToCart(1)
			.verifyAndAcceptAlert("Product added")
	}

}