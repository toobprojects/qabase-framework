package com.toob.qabase.http

import com.toob.qabase.QaBaseTest
import com.toob.qabase.http.RestModuleConstants.AUTHORIZATION
import com.toob.qabase.http.RestModuleConstants.BEARER
import com.toob.qabase.http.support.HttpSupport.serialisationConfig
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.config.ObjectMapperConfig.objectMapperConfig
import io.restassured.http.ContentType
import io.restassured.mapper.ObjectMapperType
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired


/**
 * Base class for HTTP integration tests.
 *
 * Sets up RestAssured with necessary configurations, such as base URI, content type, and authentication headers,
 * using values from [RestModuleConfigs]. Also configures JSON serialization and RestAssured's object mapper.
 */
@QaBaseTest
abstract class AbstractHttpTest {

	@Autowired
	/**
	 * Holds configuration values for the REST module, such as base URL and authentication token.
	 */
	lateinit var restModuleConfigs: RestModuleConfigs

	/**
	 * JSON serialization configuration used for serializing and deserializing HTTP bodies.
	 */
	val jsonConfig = serialisationConfig()

	/**
	 * Sets up RestAssured to explicitly use the Jackson object mapper for JSON serialization and deserialization.
	 * This ensures consistent object mapping across all HTTP tests.
	 */
	companion object {
		init {
			RestAssured.config = RestAssured.config()
				.objectMapperConfig(
					objectMapperConfig()
						.defaultObjectMapperType(ObjectMapperType.JACKSON_2)
				)
		}
	}


	@BeforeAll
	/**
	 * Resets and configures RestAssured before all tests.
	 *
	 * This function:
	 * - Resets all RestAssured static state to ensure a clean slate.
	 * - Sets up the base URI, content type, and authorization header using [restModuleConfigs].
	 */
	fun setup() {
		// Wipe out all RestAssured memory —
		// Forget every baseUri, basePath, token, header, config —
		// Start clean, like a brand-new test suite.
		RestAssured.reset()

		// Only set these up if the Rest Module Configs have been initialized.
		// Use .let{} to ensure restModuleConfigs is available.
		restModuleConfigs.let {
			// Configure RestAssured's request specification with base URI, JSON content type, and authorization header.
			RestAssured.requestSpecification = given()
				.baseUri(restModuleConfigs.baseUrl)
				.contentType(ContentType.JSON)
				.header(AUTHORIZATION, "$BEARER ${restModuleConfigs.token}")
		}
	}
}