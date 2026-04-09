package com.toob.qabase.rest.client

import io.restassured.builder.ResponseBuilder
import io.restassured.common.mapper.TypeRef
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class RestResponseTest {

	private companion object {
		private const val USERS_JSON = """
				[
				  {"id": 1, "name": "a"},
				  {"id": 2, "name": "b"}
				]
			"""
		private const val USER_ALPHA = """
				{
				  "id": 1,
				  "name": "alpha"
				}
			"""
	}

	@Test
	fun `client chain can assert directly without separate expect call`() {
		val wrapper = RestResponse(jsonResponse())

		assertSame(
			wrapper,
			wrapper
				.ok()
				.contentType()
				.fieldEq("id", 1)
				.timeUnder(50L)
				.attachIf(false)
		)
	}

	@Test
	fun `expect can still be reached explicitly from wrapped response`() {
		val wrapper = RestResponse(jsonResponse(status = 204))

		wrapper.expect()
			.statusIn(200, 201, 204)
			.noContent()
	}

	@Test
	fun `as class deserializes from wrapped response`() {
		val user = RestResponse(jsonResponse())
			.ok()
			.`as`(User::class.java)

		assertEquals(1, user.id)
		assertEquals("alpha", user.name)
	}

	@Test
	fun `as type ref deserializes collections from wrapped response`() {
		val users: List<User> = RestResponse(jsonResponse(body = USERS_JSON))
			.ok()
			.`as`(object : TypeRef<List<User>>() {})

		assertEquals(2, users.size)
	}

	@Test
	fun `raw exposes original response for escape hatch access`() {
		val raw = jsonResponse(status = 202)
		val wrapper = RestResponse(raw)

		assertSame(raw, wrapper.raw())
		assertEquals(202, wrapper.raw().statusCode)
	}

	private fun jsonResponse(
		status: Int = 200,
		contentType: String = ContentType.JSON.toString(),
		body: String = USER_ALPHA
	): Response = ResponseBuilder()
		.setStatusCode(status)
		.setContentType(contentType)
		.setBody(body)
		.build()
}

private data class User(val id: Int, val name: String)
