package com.toob.qabase.rest.assertions

import io.restassured.response.Response

object RestAssertions {

	@JvmStatic
	fun expect(response: Response): RestExpect = RestExpect(response)
}