package com.toob.qabase.rest.client

import com.toob.qabase.core.AllureExtensions.step
import com.toob.qabase.rest.RestModuleConstants
import com.toob.qabase.rest.loadRestConfig
import com.toob.qabase.rest.support.HttpSupport
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.FilterableRequestSpecification


/**
 * Centralized HTTP client for sending RESTful API requests.
 *
 * This object provides static methods to perform HTTP operations (GET, POST, PUT, DELETE)
 * using RestAssured as the underlying library. It also integrates with Allure for reporting
 * by attaching request and response details to the test report.
 *
 * All methods are wrappers over RestAssured to streamline API testing and reporting.
 */
object RestClient {

    private const val HTTP_GET = "GET"
    private const val HTTP_POST = "POST"
    private const val HTTP_PUT = "PUT"
    private const val DELETE = "DELETE"

    /**
     * Sends a HTTP GET request to the specified endpoint.
     *
     * @param endpoint The URL or path to send the GET request to.
     * @return The HTTP response from the server.
     */
    @JvmStatic
    fun get(endpoint: String): RestResponse =
        request(HTTP_GET, endpoint)

    /**
     * Sends a HTTP GET request to the specified endpoint with query parameters.
     *
     * @param endpoint The URL or path to send the GET request to.
     * @param queryParams Map of query parameter names and values to include in the request.
     * @return The HTTP response from the server.
     */
    @JvmStatic
    fun get(endpoint: String, queryParams: Map<String, Any?>? = null): RestResponse =
        request(HTTP_GET, endpoint, queryParams = queryParams)

	@JvmStatic
	fun get(
		endpoint: String,
		queryParams: Map<String, Any?>? = null,
		headers: Map<String, Any?>? = null
	): RestResponse = request(HTTP_GET, endpoint, queryParams = queryParams, headers = headers)

    /**
     * Sends a HTTP POST request to the specified endpoint with a request body.
     *
     * @param endpoint The URL or path to send the POST request to.
     * @param body The request body to send (will be serialized as JSON).
     * @return The HTTP response from the server.
     */
    @JvmStatic
    fun post(endpoint: String, body: Any): RestResponse =
        request(HTTP_POST, endpoint, body)

	@JvmStatic
	fun post(endpoint: String, body: Any, headers: Map<String, Any?>? = null): RestResponse =
		request(HTTP_POST, endpoint, body = body, headers = headers)

    /**
     * Sends a HTTP PUT request to the specified endpoint with a request body.
     *
     * @param endpoint The URL or path to send the PUT request to.
     * @param body The request body to send (will be serialized as JSON).
     * @return The HTTP response from the server.
     */
    @JvmStatic
    fun put(endpoint: String, body: Any): RestResponse =
        request(HTTP_PUT, endpoint, body)

	@JvmStatic
	fun put(endpoint: String, body: Any, headers: Map<String, Any?>? = null): RestResponse =
		request(HTTP_PUT, endpoint, body = body, headers = headers)

    /**
     * Sends a HTTP DELETE request to the specified endpoint.
     *
     * @param endpoint The URL or path to send the DELETE request to.
     * @return The HTTP response from the server.
     */
    @JvmStatic
    fun delete(endpoint: String): RestResponse =
        request(DELETE, endpoint)

	@JvmStatic
	fun delete(endpoint: String, headers: Map<String, Any?>? = null): RestResponse =
		request(DELETE, endpoint, headers = headers)

    /**
     * Sends an HTTP request with the specified method, endpoint, optional body, and optional query parameters.
     *
     * @param method The HTTP method to use (GET, POST, PUT, DELETE, etc.).
     * @param endpoint The URL or path to send the request to.
     * @param body The request body to send (for POST/PUT requests), or null for no body.
     * @param queryParams Optional map of query parameter names and values to include in the request.
     * @return The HTTP response from the server.
     */
    @JvmStatic
    fun request(
        method: String,
        endpoint: String,
        body: Any? = null,
        queryParams: Map<String, Any?>? = null,
		headers: Map<String, Any?>? = null
    ): RestResponse {
        val response = step("Sending HTTP [ $method ] request to -> ${endpoint}") {
            prepareRequest(body = body, queryParams = queryParams, headers = headers)
                // Send the HTTP request
                .`when`()
                .request(method, endpoint)
                .then()
                .extract().response().apply {
                    // Attach JSON Response to Allure Report
                    HttpSupport.attachResponse(this)
                }
        }
        return RestResponse(response)
    }

	internal fun prepareRequest(
		body: Any? = null,
		queryParams: Map<String, Any?>? = null,
		headers: Map<String, Any?>? = null
	): FilterableRequestSpecification {
		val spec = RestAssured.given() as FilterableRequestSpecification
		applyDefaultHeaders(spec, headers)
		queryParams?.let { spec.queryParams(it) }
		if (!hasHeader(spec, RestModuleConstants.CONTENT_TYPE)) {
			spec.contentType(ContentType.JSON)
		}
		body?.let {
			spec.body(it)
			HttpSupport.attachRequest(it)
		}
		return spec
	}

	private fun applyDefaultHeaders(
		spec: io.restassured.specification.RequestSpecification,
		overrideHeaders: Map<String, Any?>?
	) {
		val cfgHeaders = loadRestConfig().headers()
		val mergedHeaders = linkedMapOf<String, Any>()

		cfgHeaders.contentType().orElse(null)?.takeIf { it.isNotBlank() }?.let {
			mergedHeaders[RestModuleConstants.CONTENT_TYPE] = it
		}
		cfgHeaders.accept().orElse(null)?.takeIf { it.isNotBlank() }?.let {
			mergedHeaders[RestModuleConstants.ACCEPT] = it
		}
		cfgHeaders.authorization().orElse(null)?.takeIf { it.isNotBlank() }?.let {
			mergedHeaders[RestModuleConstants.AUTHORIZATION] = it
		}
		overrideHeaders
			.orEmpty()
			.forEach { (name, value) ->
				when (value) {
					null -> Unit
					is String -> if (value.isNotBlank()) mergedHeaders[name] = value
					else -> mergedHeaders[name] = value
				}
			}

		if (mergedHeaders.isNotEmpty()) {
			spec.headers(mergedHeaders)
		}
	}

	private fun hasHeader(
		spec: io.restassured.specification.RequestSpecification,
		headerName: String
	): Boolean =
		runCatching {
			val filterable = spec as io.restassured.specification.FilterableRequestSpecification
			filterable.headers.hasHeaderWithName(headerName)
		}.getOrDefault(false)
}
