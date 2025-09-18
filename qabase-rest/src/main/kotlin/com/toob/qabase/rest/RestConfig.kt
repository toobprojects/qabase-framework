package com.toob.qabase.rest

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault
import org.eclipse.microprofile.config.ConfigProvider
import io.smallrye.config.SmallRyeConfig
import io.smallrye.config.SmallRyeConfigBuilder
import java.util.NoSuchElementException

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
}

/**
 * Loads the RestConfig mapping.
 *
 * 1. Try to use the current ConfigProvider (what a runtime would normally give us).
 * 2. If the mapping is not found there, fall back to creating our own SmallRyeConfig
 *    with default + discovered sources so that application.yaml is read.
 */
fun loadRestConfig(): RestConfig {
    val currentConfig = ConfigProvider.getConfig()

    // Case 1: ConfigProvider already gives us a SmallRyeConfig
    if (currentConfig is SmallRyeConfig) {
        return runCatching {
            // Try to get the config mapping from the current SmallRyeConfig
            currentConfig.getConfigMapping(RestConfig::class.java)
        }.getOrElse { exception ->
            if (exception is NoSuchElementException) {
                // Mapping not registered -> build our own config and get the mapping from there
                bootstrapRestConfig().getConfigMapping(RestConfig::class.java)
            } else {
                throw exception
            }
        }
    }

    // Case 2: ConfigProvider is not SmallRyeConfig -> always bootstrap
    return bootstrapRestConfig().getConfigMapping(RestConfig::class.java)
}

private fun bootstrapRestConfig(): SmallRyeConfig =
    SmallRyeConfigBuilder()
        .withMapping(RestConfig::class.java)
        .addDefaultSources()
        .addDiscoveredSources()
        .addDiscoveredConverters()
        .build()