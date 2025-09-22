package com.toob.qabase.core.config

// Imports required for SmallRye configuration handling and Kotlin reflection
import io.smallrye.config.SmallRyeConfig
import io.smallrye.config.SmallRyeConfigBuilder
import org.eclipse.microprofile.config.ConfigProvider
import kotlin.reflect.KClass

/**
 * Utility singleton for loading configuration mappings using SmallRye Config.
 * Provides a method to load a configuration mapping for a given type, either from the current config
 * or by bootstrapping a new SmallRyeConfig instance with default and discovered sources and converters.
 */
object ConfigLoader {

	/**
	 * Loads a configuration mapping of the specified type [T].
	 *
	 * Attempts to retrieve the mapping from the current configuration if it is a SmallRyeConfig.
	 * If not available, bootstraps a new SmallRyeConfig with default and discovered sources and converters,
	 * applies an optional registration function to the builder, and returns the mapped configuration.
	 *
	 * @param type The Kotlin class of the configuration mapping interface.
	 * @param registerInBootstrap Optional lambda to customize the SmallRyeConfigBuilder before building.
	 * @return An instance of the configuration mapping of type [T].
	 */
	@JvmStatic
	fun <T: Any> loadMapping(type: KClass<T>, registerInBootstrap: (SmallRyeConfigBuilder) -> SmallRyeConfigBuilder = { it }): T {
		// Retrieve the current MicroProfile config instance
		val current = ConfigProvider.getConfig()

		// Check if the current config is an instance of SmallRyeConfig to access SmallRye-specific features
		if (current is SmallRyeConfig) {
			// Try to get the config mapping for the given type safely, catching any exceptions
			val mapped = runCatching { current.getConfigMapping(type.java) }.getOrNull()
			// If the mapping was successfully retrieved, return it directly
			if (mapped != null) return mapped
		}

		// Fallback: bootstrap a new SmallRyeConfigBuilder to build a config mapping manually
		val built = registerInBootstrap(
			SmallRyeConfigBuilder()
				// Add default config sources such as system properties and environment variables
				.addDefaultSources()
				// Add any discovered config sources registered via service loader
				.addDiscoveredSources()
				// Add any discovered converters registered via service loader
				.addDiscoveredConverters()
				// Register the mapping for the requested type
				.withMapping(type.java)
		).build()

		// Return the config mapping instance from the newly built config
		return built.getConfigMapping(type.java)
	}

}