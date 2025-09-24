package com.toob.qabase.rest

import com.toob.qabase.rest.support.HttpSupport
import io.restassured.RestAssured
import io.restassured.config.HttpClientConfig
import io.restassured.config.ObjectMapperConfig
import io.restassured.http.ContentType
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * JUnit 5 extension that configures RestAssured from SmallRye config before all tests
 * and resets after all tests. No DI required.
 */
class RestAssuredExtension : BeforeAllCallback, AfterAllCallback {
    override fun beforeAll(context: ExtensionContext) {
        // Clean slate
        RestAssured.reset()

		// Load qabase.rest.* from application.yml
		val cfg = loadRestConfig()

		// Global object mapper (Jackson) and HTTP client defaults (driven by cfg)

        // Global object mapper and HTTP client
        RestAssured.config = RestAssured.config()
            .objectMapperConfig(
                ObjectMapperConfig.objectMapperConfig()
                    .jackson2ObjectMapperFactory { _, _ -> HttpSupport.mapper }
            )
            .httpClient(
                HttpClientConfig.httpClientConfig()
                    .reuseHttpClientInstance()
                    // Default safety timeouts; can be overridden via qabase.rest.timeout-ms
                    .setParam("http.connection.timeout", 3000)
                    .setParam("http.socket.timeout", 5000)
            )

        // Helpful when an assertion fails
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

		// Set base URL on global and default request specification
		RestAssured.baseURI = cfg.baseUrl()
		val defaultSpec = RestAssured.given().contentType(ContentType.JSON)
		RestAssured.requestSpecification = defaultSpec.baseUri(cfg.baseUrl())

    }

    override fun afterAll(context: ExtensionContext) {
        // Reset static state between test classes
        RestAssured.reset()
    }
}