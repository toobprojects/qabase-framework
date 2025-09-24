package com.toob.qabase.rest.assertions

import io.restassured.builder.ResponseBuilder
import io.restassured.common.mapper.TypeRef
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame


class RestExpectTest {

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
	fun `status & contentType & fieldEq`() {
		val resp = jsonResponse()
		expect(resp)
			.ok()
			.contentType(ContentType.JSON.toString())
			.fieldEq("id", 1)
	}

	@Test
	fun `statusIn and statusFamily`() {
		val resp = jsonResponse(status = 204)
		expect(resp)
			.statusIn(200, 201, 204)
			.statusFamily(StatusFamily.SUCCESS)
	}

	@Test
	fun `emptyOrSizeAtMost on array`() {
		val resp = jsonResponse(body = """[1,2,3]""")
		expect(resp).emptyOrSizeAtMost(3)
	}

	@Test
	fun `as(Class) deserializes`() {
		data class User(val id: Int, val name: String)

		val resp = jsonResponse()
		val u = expect(resp).ok().`as`(User::class.java)
		assertEquals(1, u.id); assertEquals("alpha", u.name)
	}

	@Test
	fun `as(TypeRef) deserializes list`() {
		val resp = jsonResponse(body = USERS_JSON)

		val users: List<User> = expect(resp).ok().`as`(object : TypeRef<List<User>>() {})
		assertEquals(2, users.size)
	}

	@Test
	fun `timeUnder - synthetic responses are fast`() {
		val resp = jsonResponse()
		expect(resp).timeUnder(50L) // should be well under
	}

	@Test
	fun `attach and attachIf return same instance (fluent) and do not throw`() {
		val resp = jsonResponse()
		val e = expect(resp)
		assertSame(e, e.attach("Body"))
		assertSame(e, e.attachIf(true, "Body"))
		assertSame(e, e.attachIf(false, "Body"))
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

// at top of RestExpectTest.kt, after imports:
private data class User(val id: Int, val name: String)