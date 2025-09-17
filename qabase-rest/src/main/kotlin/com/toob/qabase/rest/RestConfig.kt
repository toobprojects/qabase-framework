package com.toob.qabase.rest

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault
import org.eclipse.microprofile.config.ConfigProvider
import io.smallrye.config.SmallRyeConfig

/**
 * Type-safe, DI-free config mapping for the "qabase.rest" prefix.
 * SmallRye generates an implementation at runtime; you just call the getters.
 *
 * Example YAML:
 * qabase:
 *   rest:
 *     base-url: https://api.example.com
 *     timeout-ms: 20000
 *     auth-token: ${API_TOKEN:}
 */
@ConfigMapping(prefix = "qabase.rest")
interface RestConfig {
    /** Base URL for REST calls. Example: https://api.example.com */
    @WithDefault("http://localhost:8080")
    fun baseUrl(): String

    /** Request/connection timeouts (ms). */
    @WithDefault("15000")
    fun timeoutMs(): Long

    /** Optional OAuth2 token. Empty string means "no token". */
    @WithDefault("")
    fun authToken(): String
}

/** Loads a SmallRye-generated implementation of RestConfig (no DI/context required). */
fun loadRestConfig(): RestConfig {
    val cfg = ConfigProvider.getConfig() as SmallRyeConfig
    return cfg.getConfigMapping(RestConfig::class.java)
}