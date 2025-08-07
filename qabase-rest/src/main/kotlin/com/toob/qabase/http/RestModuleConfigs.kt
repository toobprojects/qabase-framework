package com.toob.qabase.http

import com.toob.qabase.http.RestModuleConstants.REST_SERVICE
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = REST_SERVICE)
class RestModuleConfigs {
    lateinit var baseUrl: String
    lateinit var token: String
}