package com.toob.qabase.config

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class QbConfigTest {

	@Test
	fun `Read YAML Config File`() {
		val enabled = QbConfig.getBoolean("qabase.core.reports.archive.enabled")
		assertFalse{enabled}
	}
}