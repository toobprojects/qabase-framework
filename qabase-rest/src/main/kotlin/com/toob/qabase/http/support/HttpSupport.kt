package com.toob.qabase.http.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.toob.qabase.core.AllureExtensions
import com.toob.qabase.core.AllureExtensions.step
import io.restassured.response.Response
import kotlinx.serialization.json.Json
import kotlin.test.assertNotNull


object HttpSupport {

	private val mapper = ObjectMapper()

	@JvmStatic
	fun serialisationConfig(): Json =
		Json {
			prettyPrint = true
			ignoreUnknownKeys = true
			encodeDefaults = true
		}


	/**
	 * This function attaches the API response body to the Allure report.
	 * It helps in debugging by logging the API response when a te˚st runs.
	 *
	 * It calls Allure.addAttachment(...), which:
	 *      Adds a title: "Response Body"
	 *      Specifies the content type as JSON
	 *      Reads the response body and attaches it to the report
	 *
	 * The JSON response will now be visibly attached in the Allure report.
	 */
	@JvmStatic
	fun attachResponse(response: Response) {
        // Convert the response body to a map (or your desired format)
		val responseBodyJson = response.body().asPrettyString()
		if (responseBodyJson.isNotBlank()) {
			AllureExtensions.attachJson("Response Body", responseBodyJson)
		}
	}

	/**
	 * Attaches request body to the Allure Reports.
	 */
	@JvmStatic
	fun attachRequest(body: Any?) {
		// Attach the content
		toPrettyJson(body)?.let { jsonBody ->
			AllureExtensions.attachJson("Request Body", jsonBody)
		}
	}

	/**
	 * Verifies and logs HTTP Status 200
	 * It also logs on Allure Reports
	 */
	@JvmStatic
	fun allOkay(response: Response) =
		verifyStatusCode(response, 200, "✅ HTTP OK confirmation (200)")

	/**
	 * Verifies and logs HTTP Status 201
	 * It also logs on Allure Reports
	 */
	@JvmStatic
	fun created(response: Response) =
		verifyStatusCode(response, 201, "✅ Record successfully Created (201)")

	/**
	 * Verifies and logs HTTP Status 204
	 * It also logs on Allure Reports
	 */
	@JvmStatic
	fun allOkayWithoutContent(response: Response) =
		verifyStatusCode(response, 204, "✅ No Content but request successful (204)")

	/**
	 * Used to validate / assert HTTP Status Code that we would have gotten from the service.
	 * @param expectedCode : Int → Expected HTTP Status Code.
	 * @param response : Response → REST Assured HTTP Response Object
	 */
	@JvmStatic
	fun verifyStatusCode(response: Response, expectedCode: Int, message: String) {
		assertNotNull(response)
		step(message) {
			response.then().statusCode(expectedCode)
		}
	}

	/**
	 * Converts the given Body to JSON String.
	 * @param obj : Any → Body to be converted to JSON
	 */
	@JvmStatic
	fun toJson(obj: Any?): String? {
		return try {
			obj?.let { mapper.writeValueAsString(it) }
		} catch (exception: Exception) {
			null
		}
	}

	/**
	 * Converts the given Body to PRETTY JSON String.
	 * @param obj : Any → Body to be converted to JSON
	 */
	@JvmStatic
	fun toPrettyJson(obj: Any?): String? {
		return try {
			obj?.let {
				mapper
					.writerWithDefaultPrettyPrinter()
					.writeValueAsString(it)
			}
		} catch (exception: Exception) {
			null
		}
	}
}