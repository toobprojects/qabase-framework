package com.toob.qabase.core.config


import io.smallrye.config.SmallRyeConfig
import io.smallrye.config.SmallRyeConfigBuilder
import org.eclipse.microprofile.config.ConfigProvider
import kotlin.reflect.KClass

object ConfigLoader {

	@JvmStatic
	fun <T: Any> loadMapping(type: KClass<T>, registerInBootstrap: (SmallRyeConfigBuilder) -> SmallRyeConfigBuilder = { it }): T {
		val current = ConfigProvider.getConfig()
		if (current is SmallRyeConfig) {
			val mapped = runCatching { current.getConfigMapping(type.java) }.getOrNull()
			if (mapped != null) return mapped
		}
		// Fallback bootstrap
		val built = registerInBootstrap(
			SmallRyeConfigBuilder()
				.addDefaultSources()
				.addDiscoveredSources()
				.addDiscoveredConverters()
				.withMapping(type.java)
		).build()
		return built.getConfigMapping(type.java)
	}

}