package com.toob.qabase.rest

import io.smallrye.config.SmallRyeConfig
import io.smallrye.config.SmallRyeConfigBuilder
import io.smallrye.config.SmallRyeConfigProviderResolver
import io.smallrye.config.common.MapBackedConfigSource
import org.eclipse.microprofile.config.spi.ConfigSource
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

class RestConfigTest {

	private lateinit var original: SmallRyeConfigProviderResolver

	@BeforeEach
	fun setUp() {
		// no-op: we’ll install a test config per test
	}

	@AfterEach
	fun tearDown() {
		SmallRyeConfigProviderResolver.instance().releaseConfig(
			SmallRyeConfigProviderResolver.instance().getConfig(javaClass.classLoader)
		)
	}

	@Test
	fun `Default Configs Kick In`() {
		val cfg = SmallRyeConfigBuilder()
			.withMapping(RestConfig::class.java)
			.withSources(
				object : MapBackedConfigSource(
					"test-flags",
					mapOf("smallrye.config.allowEmptyValues" to "true"),
					100
				) {}
			)
			.build()

		SmallRyeConfigProviderResolver.instance().registerConfig(cfg, javaClass.classLoader)

		val rc = loadRestConfig()
		assertEquals("http://localhost:8080", rc.baseUrl())
	}

	@Test
	fun `Overrides Are Mapped`() {
		val cfg: SmallRyeConfig = SmallRyeConfigBuilder()
			.withMapping(RestConfig::class.java)
			.withSources(
				object :MapBackedConfigSource(
					"test-overrides",
					mapOf("qabase.rest.base-url" to "https://api.example.com"),
					100
				) {}
			)
			.build()

		SmallRyeConfigProviderResolver.instance().registerConfig(cfg, javaClass.classLoader)

		val rc = loadRestConfig()
		assertEquals("https://api.example.com", rc.baseUrl())
	}

	@Test
	fun `Actual Config Values Are loaded!`() {
		val rc = loadRestConfig()
		assertEquals("https://jsonplaceholder.typicode.com", rc.baseUrl())
	}
}