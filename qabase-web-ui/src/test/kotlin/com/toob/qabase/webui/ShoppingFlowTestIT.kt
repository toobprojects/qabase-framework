package com.toob.qabase.webui

import com.toob.qabase.webui.flows.ShoppingFlows
import kotlin.test.Test

@QaWebUiTest
class ShoppingFlowTestIT {

	private val shopping = ShoppingFlows()

	@Test
	fun addItem() {
		shopping.addSamsungS6ToCart()
	}
}