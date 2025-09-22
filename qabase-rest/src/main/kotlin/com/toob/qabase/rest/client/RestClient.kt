package com.toob.qabase.rest.client

import com.toob.qabase.core.AllureExtensions.step
import com.toob.qabase.rest.support.HttpSupport
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response


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
    fun get(endpoint: String): Response =
        request(HTTP_GET, endpoint)

    /**
     * Sends a HTTP GET request to the specified endpoint with query parameters.
     *
     * @param endpoint The URL or path to send the GET request to.
     * @param queryParams Map of query parameter names and values to include in the request.
     * @return The HTTP response from the server.
     */
    @JvmStatic
    fun get(endpoint: String, queryParams: Map<String, Any?>? = null): Response =
        request(HTTP_GET, endpoint, queryParams = queryParams)

    /**
     * Sends a HTTP POST request to the specified endpoint with a request body.
     *
     * @param endpoint The URL or path to send the POST request to.
     * @param body The request body to send (will be serialized as JSON).
     * @return The HTTP response from the server.
     */
    @JvmStatic
    fun post(endpoint: String, body: Any): Response =
        request(HTTP_POST, endpoint, body)

    /**
     * Sends a HTTP PUT request to the specified endpoint with a request body.
     *
     * @param endpoint The URL or path to send the PUT request to.
     * @param body The request body to send (will be serialized as JSON).
     * @return The HTTP response from the server.
     */
    @JvmStatic
    fun put(endpoint: String, body: Any): Response =
        request(HTTP_PUT, endpoint, body)

    /**
     * Sends a HTTP DELETE request to the specified endpoint.
     *
     * @param endpoint The URL or path to send the DELETE request to.
     * @return The HTTP response from the server.
     */
    @JvmStatic
    fun delete(endpoint: String): Response =
        request(DELETE, endpoint)

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
        queryParams: Map<String, Any?>? = null
    ): Response {
        return step("Sending HTTP [ $method ] request to -> ${endpoint}") {
            RestAssured.given()
                // Set the request content type to JSON
                .contentType(ContentType.JSON)
                .apply {
                    // Apply query parameters if present
                    queryParams?.let {
                        queryParams(it)
                    }
                    // Attach request body if present and add it to Allure report
                    body?.let {
                        body(it)
                        // Attach JSON Request to Allure Report
                        HttpSupport.attachRequest(it)
                    }
                }
                // Send the HTTP request
                .`when`()
                .request(method, endpoint)
                .then()
                .extract().response().apply {
                    // Attach JSON Response to Allure Report
                    HttpSupport.attachResponse(this)
                }
        }
    }
}