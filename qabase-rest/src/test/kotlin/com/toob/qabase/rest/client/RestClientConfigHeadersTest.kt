package com.toob.qabase.rest.client

import io.restassured.RestAssured
import io.restassured.specification.FilterableRequestSpecification
import io.smallrye.config.SmallRyeConfigBuilder
import io.smallrye.config.SmallRyeConfigProviderResolver
import io.smallrye.config.common.MapBackedConfigSource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RestClientConfigHeadersTest {

	@AfterEach
	fun tearDown() {
		RestAssured.reset()
		runCatching {
			SmallRyeConfigProviderResolver.instance().releaseConfig(
				SmallRyeConfigProviderResolver.instance().getConfig(javaClass.classLoader)
			)
		}
	}

	@Test
	fun `config based headers are applied automatically`() {
		registerConfig(
			mapOf(
				"qabase.rest.base-url" to "https://api.example.com",
				"qabase.rest.headers.content-type" to "application/xml",
				"qabase.rest.headers.accept" to "application/xml",
				"qabase.rest.headers.authorization" to "Bearer config-token"
			)
		)

		val spec = RestClient.prepareRequest() as FilterableRequestSpecification

		assertEquals("application/xml", spec.contentType)
		assertEquals("application/xml", spec.headers.getValue("Accept"))
		assertEquals("Bearer config-token", spec.headers.getValue("Authorization"))
	}

	@Test
	fun `request headers override config defaults and can add new ones`() {
		registerConfig(
			mapOf(
				"qabase.rest.base-url" to "https://api.example.com",
				"qabase.rest.headers.content-type" to "application/json",
				"qabase.rest.headers.accept" to "application/json",
				"qabase.rest.headers.authorization" to "Bearer config-token"
			)
		)

		val spec = RestClient.prepareRequest(
			headers = mapOf(
				"Accept" to "application/problem+json",
				"Authorization" to "Bearer request-token",
				"X-Test-Trace" to "trace-123"
			)
		) as FilterableRequestSpecification

		assertEquals("application/json", spec.contentType)
		assertEquals("application/problem+json", spec.headers.getValue("Accept"))
		assertEquals("Bearer request-token", spec.headers.getValue("Authorization"))
		assertEquals("trace-123", spec.headers.getValue("X-Test-Trace"))
	}

	@Test
	fun `requests without configured content type still default to json`() {
		registerConfig(
			mapOf(
				"qabase.rest.base-url" to "https://api.example.com"
			)
		)

		val spec = RestClient.prepareRequest() as FilterableRequestSpecification

		assertEquals("application/json", spec.contentType)
	}

	private fun registerConfig(properties: Map<String, String>) {
		runCatching {
			SmallRyeConfigProviderResolver.instance().releaseConfig(
				SmallRyeConfigProviderResolver.instance().getConfig(javaClass.classLoader)
			)
		}

		val cfg = SmallRyeConfigBuilder()
			.withMapping(com.toob.qabase.rest.RestConfig::class.java)
			.withSources(
				object : MapBackedConfigSource("test-config", properties, 100) {}
			)
			.build()

		SmallRyeConfigProviderResolver.instance().registerConfig(cfg, javaClass.classLoader)
	}
}
