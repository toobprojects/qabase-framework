package com.toob.qabase.http

import com.toob.qabase.core.AllureExtensions.step
import com.toob.qabase.http.support.HttpSupport
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response


object RestClient {

    private const val HTTP_GET = "GET"
    private const val HTTP_POST = "POST"
    private const val HTTP_PUT = "PUT"
    private const val DELETE = "DELETE"

    @JvmStatic
    fun get(endpoint: String): Response =
        request(HTTP_GET, endpoint)

    @JvmStatic
    fun get(endpoint: String, queryParams: Map<String, Any?>? = null): Response
        = request(HTTP_GET, endpoint)

    @JvmStatic
    fun post(endpoint: String, body: Any): Response =
        request(HTTP_POST, endpoint, body)

    @JvmStatic
    fun put(endpoint: String, body: Any): Response =
        request(HTTP_PUT, endpoint, body)

    @JvmStatic
    fun delete(endpoint: String): Response =
        request(DELETE, endpoint)

    
    @JvmStatic
    fun request(
        method: String,
        endpoint: String,
        body: Any? = null,
        queryParams: Map<String, Any?>? = null
    ): Response {
        return step("Sending HTTP [ $method ] request to -> ${endpoint}") {
            RestAssured.given()
                .contentType(ContentType.JSON)
                .apply {

                    // For cases where we have queryParams
                    queryParams?.let {
                        queryParams(it)
                    }

                    // Case where we have a Request Body
                    body?.let {
                        body(it)

                        // Attach JSON Request to Allure Report
                        HttpSupport.attachRequest(it)
                    }
                }
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