package com.toob.qabase.rest.support

import com.toob.qabase.core.AllureExtensions
import com.toob.qabase.rest.RestModuleConstants
import io.restassured.response.Response
import org.hamcrest.Matchers

/**
 * Provides expectation methods for validating REST API responses.
 */
class RestExpect(private val response: Response) {

	/**
	 * Verifies that the JSON field at the given path equals the expected value.
	 */
	fun fieldEq(path: String, expected: Any?): RestExpect {
		AllureExtensions.step("Expect JSON field '$path' == '$expected'") {
			response.then().body(path, Matchers.equalTo(expected))
		}
		return this
	}

	/**
	 * Verifies that the JSON field at the given path equals the expected value.
	 */
	fun contentType(expected: String = RestModuleConstants.MEDIA_JSON): RestExpect {
		AllureExtensions.step("Expect content type $expected") {
			response.then().contentType(expected)
		}
		return this
	}

	/**
	 * Verifies that the JSON field at the given path equals the expected value.
	 */
	fun timeUnder(millis: Long): RestExpect {
		AllureExtensions.step("Expect response time <= $millis ms") {
			response.then().time(Matchers.lessThan(millis))
		}
		return this
	}

	/**
	 * Verifies that the JSON field at the given path equals the expected value.
	 */
	fun emptyOrSizeAtMost(max: Int): RestExpect {
		AllureExtensions.step("Expect JSON body size() <= $max") {
			response.then().body("size()", Matchers.lessThanOrEqualTo(max))
		}
		return this
	}
}