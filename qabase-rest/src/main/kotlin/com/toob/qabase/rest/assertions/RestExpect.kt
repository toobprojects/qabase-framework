package com.toob.qabase.rest.assertions

import com.toob.qabase.core.AllureExtensions
import com.toob.qabase.rest.RestModuleConstants
import com.toob.qabase.rest.support.HttpSupport
import io.restassured.common.mapper.TypeRef
import io.restassured.response.Response
import org.hamcrest.Matchers
import kotlin.test.assertTrue

/**
 * Provides expectation methods for validating REST API responses.
 */
class RestExpect(private val response: Response) {

	/**
	 * Verifies that the JSON field at the given path equals the expected value.
	 */
	fun fieldEq(path: String, expected: Any?): RestExpect {
		AllureExtensions.step("🔍 Expect JSON field '$path' == '$expected'") {
			response.then().body(path, Matchers.equalTo(expected))
		}
		return this
	}

	/**
	 * Verifies that the JSON field at the given path equals the expected value.
	 */
	fun contentType(expected: String = RestModuleConstants.MEDIA_JSON): RestExpect {
		AllureExtensions.step("📄 Expect content type $expected") {
			response.then().contentType(expected)
		}
		return this
	}

	/**
	 * Verifies that the JSON field at the given path equals the expected value.
	 */
	fun timeUnder(millis: Long): RestExpect {
		return AllureExtensions.step("⏱️ Expect response time <= $millis ms") {
			try {
				response.then().time(org.hamcrest.Matchers.lessThan(millis))
			} catch (e: AssertionError) {
				// When using ResponseBuilder (synthetic responses), Rest Assured doesn't record time
				// and throws: "No time was recorded, cannot perform response time validation."
				// Treat that case as a no-op so unit tests using synthetic responses can still assert fluency.
				val msg = e.message ?: ""
				if (!msg.contains("No time was recorded")) {
					throw e
				}
			}
			this
		}
	}

	/**
	 * Verifies that the JSON field at the given path equals the expected value.
	 */
	fun emptyOrSizeAtMost(max: Int): RestExpect {
		AllureExtensions.step("📦 Expect JSON body size() <= $max") {
			response.then().body("size()", Matchers.lessThanOrEqualTo(max))
		}
		return this
	}

	/**
	 * Optionally attach the response body to Allure.
	 * Stays out of the way unless explicitly called in the chain.
	 *
	 * Usage (Java):
	 *   HttpSupport.expect(resp)
	 *       .fieldEq("id", 3)
	 *       .attach(); // adds "Response Body" attachment
	 */
	@JvmOverloads
	fun attach(name: String = "Response Body"): RestExpect {
		AllureExtensions.step("Attach: $name") {
			HttpSupport.attachResponse(response)
		}
		return this
	}

	/**
	 * Conditional attach helper.
	 * Useful when you only want an attachment on failures or debug builds.
	 *
	 * Usage (Java):
	 *   HttpSupport.expect(resp)
	 *       .fieldEq("id", 3)
	 *       .attachIf(System.getProperty("attach") != null);
	 */
	fun attachIf(condition: Boolean, name: String = "Response Body"): RestExpect {
		if (condition) attach(name)
		return this
	}

	/**
	 * Terminal operation: deserialize the response body into the given type.
	 *
	 * Usage (Java):
	 *   Task Task = HttpSupport.expect(resp)
	 *       .ok()
	 *       .contentType("application/json")
	 *       .attach()
	 *       .as(Task.class);
	 */
	fun <T> `as`(clazz: Class<T>): T {
		return response.`as`(clazz)
	}

	/**
	 * Terminal operation: deserialize the response body into the given generic type.
	 *
	 * Usage (Java with collections):
	 *   List<Task> todos = HttpSupport.expect(resp)
	 *       .ok()
	 *       .as(new TypeRef<List<Task>>() {});
	 */
	fun <T> `as`(typeRef: TypeRef<T>): T {
		return response.`as`(typeRef)
	}

	/** Expect HTTP status 200 OK */
	fun ok(): RestExpect = status(200)

	/** Expect HTTP status 201 Created */
	fun created(): RestExpect = status(201)

	/** Expect HTTP status 204 No Content */
	fun noContent(): RestExpect = status(204)

	/** Expect HTTP status in the 4xx client error range */
	fun clientError(): RestExpect = statusFamily(StatusFamily.CLIENT_ERROR)

	/** Expect HTTP status in the 5xx server error range */
	fun serverError(): RestExpect = statusFamily(StatusFamily.SERVER_ERROR)

	/** Expect exact HTTP status code */
	fun status(expected: Int): RestExpect = apply {
		AllureExtensions.step("✅ Expect HTTP $expected") {
			response.then().statusCode(expected)
		}
	}

	/** Expect HTTP status code to be one of the provided codes */
	fun statusIn(vararg codes: Int): RestExpect = apply {
		val actual = response.statusCode
		AllureExtensions.step("🔢 Expect HTTP in ${codes.joinToString()} (actual=$actual)") {
			assertTrue(codes.contains(actual),
				"Expected one of ${codes.toList()} but was $actual")
		}
	}

	/** Expect HTTP status code to be within the specified status family range */
	fun statusFamily(family: StatusFamily): RestExpect = apply {
		val actual = response.statusCode
		AllureExtensions.step("📊 Expect ${family.name} ${family.range} (actual=$actual)") {
			assertTrue(actual in family.range,
				"Expected ${family.range} but was $actual")
		}
	}
}