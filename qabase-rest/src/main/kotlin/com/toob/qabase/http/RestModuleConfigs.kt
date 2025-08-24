package com.toob.qabase.http

import com.toob.qabase.http.client.RestModuleConstants.REST_SERVICE
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Holds configuration properties for the QA Base REST module,
 * including the base URL and authentication token.
 */
// Binds external properties (with prefix `rest-service`) to this class
@ConfigurationProperties(prefix = REST_SERVICE)
class RestModuleConfigs {
    /** The base URL for the REST service. */
    lateinit var baseUrl: String
    /** The authentication token used for accessing the REST service. */
    lateinit var token: String
}