package com.toob.qabase.http

import com.toob.qabase.QaBaseTest
import com.toob.qabase.util.logger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@QaBaseTest
class RestModuleConfigsIT {

	private val log = logger()

    @Autowired
    lateinit var restModuleConfigs: RestModuleConfigs

    @Test
    fun `Should load configurations`() {
        assertNotNull(restModuleConfigs)
        assertNotNull( restModuleConfigs.baseUrl)
        assertEquals("https://jsonplaceholder.typicode.com", restModuleConfigs.baseUrl)
		log.info { "✅ Rest configs loaded successfully" }
    }
}