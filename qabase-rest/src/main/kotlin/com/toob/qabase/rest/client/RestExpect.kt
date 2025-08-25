package com.toob.qabase.rest.client

import io.restassured.response.Response
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.lessThan
import org.hamcrest.Matchers.lessThanOrEqualTo
import com.toob.qabase.core.AllureExtensions.step
import com.toob.qabase.rest.client.RestModuleConstants.MEDIA_JSON

class RestExpect(private val response: Response) {

	fun fieldEq(path: String, expected: Any?): RestExpect {
		step("Expect JSON field '$path' == '$expected'") {
			response.then().body(path, equalTo(expected))
		}
		return this
	}

	fun contentType(expected: String = MEDIA_JSON): RestExpect {
		step("Expect content type $expected") {
			response.then().contentType(expected)
		}
		return this
	}

	fun timeUnder(millis: Long): RestExpect {
		step("Expect response time <= $millis ms") {
			response.then().time(lessThan(millis))
		}
		return this
	}

	fun emptyOrSizeAtMost(max: Int): RestExpect {
		step("Expect JSON body size() <= $max") {
			response.then().body("size()", lessThanOrEqualTo(max))
		}
		return this
	}
}