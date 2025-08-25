package com.toob.qabase.rest.support

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.toob.qabase.core.AllureExtensions
import com.toob.qabase.core.AllureExtensions.step
import io.restassured.response.Response
import kotlinx.serialization.json.Json
import kotlin.test.assertNotNull

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
	 * Creates a Kotlinx Serialization Json configuration with:
	 * - Pretty printing enabled for readable output
	 * - Unknown keys ignored during deserialization for flexibility
	 * - Defaults encoded to ensure complete JSON representation
	 */
	@JvmStatic
	fun serialisationConfig(): Json =
		Json {
			prettyPrint = true
			ignoreUnknownKeys = true
			encodeDefaults = true
		}

	/**
	 * Attaches the API response body to the Allure report.
	 * It pretty prints and attaches the response body if it is not blank,
	 * aiding in debugging by making the response content visible in the report.
	 */
	@JvmStatic
	fun attachResponse(response: Response) {
        // Convert the response body to a pretty JSON string
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
	 * Verifies that the HTTP response status code is 200 (OK).
	 * Useful for confirming that the request was successfully processed by the server.
	 * Also logs this verification step in the Allure report.
	 */
	@JvmStatic
	fun allOkay(response: Response) =
		verifyStatusCode(response, 200, "✅ HTTP OK confirmation (200)")

	/**
	 * Verifies that the HTTP response status code is 201 (Created).
	 * Indicates that a new resource was successfully created on the server.
	 * Also logs this verification step in the Allure report.
	 */
	@JvmStatic
	fun created(response: Response) =
		verifyStatusCode(response, 201, "✅ Record successfully Created (201)")

	/**
	 * Verifies that the HTTP response status code is 204 (No Content).
	 * Indicates that the request was successful but there is no content to return.
	 * Also logs this verification step in the Allure report.
	 */
	@JvmStatic
	fun allOkayWithoutContent(response: Response) =
		verifyStatusCode(response, 204, "✅ No Content but request successful (204)")

	/**
	 * Validates the HTTP status code of the response against the expected code.
	 * Logs the verification step in Allure and asserts that the response status matches expected.
	 *
	 * @param expectedCode Int - The expected HTTP status code to verify.
	 * @param response Response - The REST Assured HTTP response object to validate.
	 * @param message String - Descriptive message for logging in Allure.
	 */
	@JvmStatic
	fun verifyStatusCode(response: Response, expectedCode: Int, message: String) {
		assertNotNull(response)
		step(message) {
			// Assert that the response status code matches the expected code
			response.then().statusCode(expectedCode)
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
	fun toJson(obj: Any?): String? {
		return try {
			obj?.let { internalMapper.writeValueAsString(it) }
		} catch (exception: Exception) {
			null
		}
	}

	/**
	 * Converts the given object to a pretty-printed JSON string.
	 * Useful for logging, debugging, or displaying JSON in a human-readable format.
	 *
	 * @param obj Any? - The object to convert to pretty JSON.
	 * @return String? - The pretty JSON string or null if conversion fails.
	 */
	@JvmStatic
	fun toPrettyJson(obj: Any?): String? {
		return try {
			obj?.let {
				internalMapper
					.writerWithDefaultPrettyPrinter()
					.writeValueAsString(it)
			}
		} catch (exception: Exception) {
			null
		}
	}

	// Public getter (works for both Kotlin & Java)
	@JvmStatic
	val mapper: ObjectMapper
		get() = internalMapper


}