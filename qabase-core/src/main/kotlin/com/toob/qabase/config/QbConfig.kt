package com.toob.qabase.config

import org.eclipse.microprofile.config.ConfigProvider

object QbConfig {

	private val cfg = ConfigProvider.getConfig()

	fun get(name: String): String? =
		cfg.getOptionalValue(name, String::class.java).orElse(null)

	fun getOrDefault(name: String, default: String): String =
		cfg.getOptionalValue(name, String::class.java).orElse(default)

	fun getBoolean(name: String, default: Boolean = false): Boolean =
		cfg.getOptionalValue(name, Boolean::class.java).orElse(default)
}