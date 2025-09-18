package com.toob.qabase.rest

import com.toob.qabase.rest.support.HttpSupport
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.FilterableRequestSpecification
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(RestAssuredExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestAssuredExtensionTest {

	@Test
	fun `should configure RestAssured from config`() {
		val cfg = loadRestConfig()

		// Base URI
		assertEquals(cfg.baseUrl(), RestAssured.baseURI)

		// Timeouts
		val params = RestAssured.config.httpClientConfig.params()

		assertEquals(3000, params["http.connection.timeout"])
		assertEquals(5000, params["http.socket.timeout"])

		// ObjectMapper is our Jackson2 instance
		val mapper = HttpSupport.mapper
		val configuredFactory = RestAssured.config.objectMapperConfig.jackson2ObjectMapperFactory()
		val actual = configuredFactory!!.create(null, null)
		assertEquals(mapper.factory.javaClass, actual.factory.javaClass)

		// Content-Type on default spec
		val spec = RestAssured.requestSpecification as FilterableRequestSpecification
		val ct = spec.contentType
		assertTrue(
			ct == ContentType.JSON.toString() || (ct != null && ct.startsWith("application/json")),
			"Default request spec should use JSON content type"
		)

		// New given() inherits base URI
		val given = RestAssured.given() as FilterableRequestSpecification
		assertEquals(cfg.baseUrl(), given.baseUri, "New requests should inherit base URI from default spec")
	}
}