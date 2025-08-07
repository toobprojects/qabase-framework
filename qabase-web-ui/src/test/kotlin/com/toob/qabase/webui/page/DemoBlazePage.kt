package com.toob.qabase.webui.page

import com.codeborne.selenide.Selenide.open
import com.codeborne.selenide.Selenide.switchTo
import com.toob.qabase.core.AllureExtensions.step

import com.toob.qabase.webui.ext.SelenideExtensions.byCss
import io.qameta.allure.Step
import org.springframework.stereotype.Component

@Component
class DemoBlazePage {

	@Step("Open DemoBlaze web page")
	fun openPage() : DemoBlazePage {
		open("https://www.demoblaze.com/")
		return this
	}

	@Step("Click the \"Samsung galaxy s6\" device")
	fun selectProduct(productId: Int) : DemoBlazePage {
		byCss("a[href='prod.html?idp_=$productId']")
			.click()
		return this
	}

	@Step("Click the Add to Card Button")
	fun addToCart(productId: Int) : DemoBlazePage {
		byCss("a[onclick='addToCart($productId)']")
			.click()
		return this
	}

	@Step("Verify and accept alert")
	fun verifyAndAcceptAlert(expectedText: String) : DemoBlazePage {
		val alert = step("Focus on the popup") {
			switchTo().alert()
		}

		step("Expect text : \"$expectedText\"") {
			assert(alert.text.contains(expectedText, ignoreCase = true)) // Corrected assertion
		}

		step("Click OK button on JS alert") {
			alert.accept() // Click OK button on alert
		}
		return this
	}

}