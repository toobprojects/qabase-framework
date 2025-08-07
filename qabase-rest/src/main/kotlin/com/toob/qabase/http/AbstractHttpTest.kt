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


@QaBaseTest
abstract class AbstractHttpTest {

	@Autowired
	lateinit var restModuleConfigs: RestModuleConfigs

	val jsonConfig = serialisationConfig()

	companion object {
		init {
			// To auto-detect the mapper without worry later ...
			// ... configure RestAssured to explicitly tell it to use Jackson, like:
			RestAssured.config = RestAssured.config()
				.objectMapperConfig(
					objectMapperConfig()
						.defaultObjectMapperType(ObjectMapperType.JACKSON_2)
				)
		}
	}


	@BeforeAll
	fun setup() {

		// Wipe out all RestAssured memory —
		// Forget every baseUri, basePath, token, header, config —
		// Start clean, like a brand-new test suite.
		RestAssured.reset()

		// Only set these up if the Rest Module Configs Have been setup.
		// To make sure of that we use the .let{}
		restModuleConfigs.let {
			RestAssured.requestSpecification = given()
				.baseUri(restModuleConfigs.baseUrl)
				.contentType(ContentType.JSON)
				.header(AUTHORIZATION, "$BEARER ${restModuleConfigs.token}")
		}
	}
}