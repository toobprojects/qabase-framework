package com.toob.qabase.rest

import io.smallrye.config.SmallRyeConfig
import io.smallrye.config.SmallRyeConfigBuilder
import io.smallrye.config.SmallRyeConfigProviderResolver
import io.smallrye.config.common.MapBackedConfigSource
import org.eclipse.microprofile.config.spi.ConfigSource
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse

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
		assertFalse(rc.headers().contentType().isPresent)
		assertFalse(rc.headers().accept().isPresent)
		assertFalse(rc.headers().authorization().isPresent)
	}

	@Test
	fun `Overrides Are Mapped`() {
		val cfg: SmallRyeConfig = SmallRyeConfigBuilder()
			.withMapping(RestConfig::class.java)
			.withSources(
				object :MapBackedConfigSource(
					"test-overrides",
					mapOf(
						"qabase.rest.base-url" to "https://api.example.com",
						"qabase.rest.headers.content-type" to "application/xml",
						"qabase.rest.headers.accept" to "application/xml",
						"qabase.rest.headers.authorization" to "Bearer override-token"
					),
					100
				) {}
			)
			.build()

		SmallRyeConfigProviderResolver.instance().registerConfig(cfg, javaClass.classLoader)

		val rc = loadRestConfig()
		assertEquals("https://api.example.com", rc.baseUrl())
		assertEquals("application/xml", rc.headers().contentType().orElseThrow())
		assertEquals("application/xml", rc.headers().accept().orElseThrow())
		assertEquals("Bearer override-token", rc.headers().authorization().orElseThrow())
	}

	@Test
	fun `Actual Config Values Are loaded!`() {
		val rc = loadRestConfig()
		assertEquals("https://jsonplaceholder.typicode.com", rc.baseUrl())
		assertEquals("application/json", rc.headers().contentType().orElseThrow())
		assertEquals("application/json", rc.headers().accept().orElseThrow())
		assertEquals("Bearer some-token", rc.headers().authorization().orElseThrow())
	}
}
