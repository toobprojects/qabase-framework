package com.toob.qabase.rest.client

import com.toob.qabase.rest.assertions.RestExpect
import com.toob.qabase.rest.assertions.StatusFamily
import com.toob.qabase.rest.RestModuleConstants
import io.restassured.common.mapper.TypeRef
import io.restassured.response.Response

/**
 * Fluent response wrapper that keeps request execution and assertions in a single chain.
 *
 * Internally it delegates to [RestExpect], but it exposes the same high-value assertion surface
 * directly so tests can read like:
 *
 * RestClient.get("/users/1")
 *   .ok()
 *   .fieldEq("id", 1)
 */
class RestResponse(private val response: Response) {

	private val expectations = RestExpect(response)

	/** Access the underlying RestAssured response when lower-level APIs are needed. */
	fun raw(): Response = response

	/** Access the dedicated expectation object explicitly when preferred. */
	fun expect(): RestExpect = expectations

	fun fieldEq(path: String, expected: Any?): RestResponse = apply {
		expectations.fieldEq(path, expected)
	}

	@JvmOverloads
	fun contentType(expected: String = RestModuleConstants.MEDIA_JSON): RestResponse = apply {
		expectations.contentType(expected)
	}

	fun timeUnder(millis: Long): RestResponse = apply {
		expectations.timeUnder(millis)
	}

	fun emptyOrSizeAtMost(max: Int): RestResponse = apply {
		expectations.emptyOrSizeAtMost(max)
	}

	@JvmOverloads
	fun attach(name: String = "Response Body"): RestResponse = apply {
		expectations.attach(name)
	}

	fun attachIf(condition: Boolean, name: String = "Response Body"): RestResponse = apply {
		expectations.attachIf(condition, name)
	}

	fun ok(): RestResponse = apply { expectations.ok() }

	fun created(): RestResponse = apply { expectations.created() }

	fun noContent(): RestResponse = apply { expectations.noContent() }

	fun clientError(): RestResponse = apply { expectations.clientError() }

	fun serverError(): RestResponse = apply { expectations.serverError() }

	fun status(expected: Int): RestResponse = apply { expectations.status(expected) }

	fun statusIn(vararg codes: Int): RestResponse = apply { expectations.statusIn(*codes) }

	fun statusFamily(family: StatusFamily): RestResponse = apply { expectations.statusFamily(family) }

	fun <T> `as`(clazz: Class<T>): T = expectations.`as`(clazz)

	fun <T> `as`(typeRef: TypeRef<T>): T = expectations.`as`(typeRef)
}
