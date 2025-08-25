package com.toob.qabase.webui.test

import com.toob.qabase.QaBaseTest
import com.toob.qabase.webui.flows.ShoppingFlows
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test

@QaBaseTest
class ShoppingFlowTest(@Autowired private val shopping: ShoppingFlows) {

	@Test
	fun addItem() {
		shopping.addSamsungS6ToCart()
	}
}