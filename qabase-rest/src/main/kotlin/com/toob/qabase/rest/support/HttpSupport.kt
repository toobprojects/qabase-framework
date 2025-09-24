package com.toob.qabase.rest.support

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.toob.qabase.core.AllureExtensions
import com.toob.qabase.rest.assertions.RestExpect
import io.restassured.response.Response

/**
 * Utility class providing common HTTP test support functions such as serialization configuration,
 * request and response logging, and HTTP status verification.
 * Integrates with Allure reports for enhanced test reporting and debugging.
 */
object HttpSupport {

	// Jackson ObjectMapper used for JSON serialization and deserialization
	private val internalMapper = ObjectMapper()
		.registerModule(KotlinModule.Builder().build())
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

	/**
	 * Attaches the API response body to the Allure report.
	 * It pretty prints and attaches the response body if it is not blank,
	 * aiding in debugging by making the response content visible in the report.
	 */
	@JvmStatic
	fun attachResponse(response: Response) {
        // Convert the response body to a pretty JSON string
		if (!AllureExtensions.allureEnabled() || !AllureExtensions.inAllureTest()) return
		val responseBodyJson = response.body().asPrettyString()
		if (responseBodyJson.isNotBlank()) {
			AllureExtensions.attachJson("Response Body", responseBodyJson)
		}
	}

	/**
	 * Converts the request body to JSON and attaches it to the Allure report.
	 * This helps in debugging by logging the exact request payload sent to the server.
	 */
	@JvmStatic
	fun attachRequest(body: Any?) {
		// Attach the content
		toPrettyJson(body)?.let { jsonBody ->
			AllureExtensions.attachJson("Request Body", jsonBody)
		}
	}

	/**
	 * Converts the given object to a compact JSON string.
	 * Useful for transmitting or storing JSON in a minimal form.
	 *
	 * @param obj Any? - The object to convert to JSON.
	 * @return String? - The compact JSON string or null if conversion fails.
	 */
	@JvmStatic
	fun toJson(value: Any?): String = internalMapper.writeValueAsString(value)

	/**
	 * Converts the given object to a pretty-printed JSON string.
	 * Useful for logging, debugging, or displaying JSON in a human-readable format.
	 *
	 * @param obj Any? - The object to convert to pretty JSON.
	 * @return String? - The pretty JSON string or null if conversion fails.
	 */
	@JvmStatic
	fun toPrettyJson(obj: Any?): String =
		internalMapper
			.writerWithDefaultPrettyPrinter()
			.writeValueAsString(obj)


	// Public getter (works for both Kotlin & Java)
	@JvmStatic
	val mapper: ObjectMapper
		get() = internalMapper
}