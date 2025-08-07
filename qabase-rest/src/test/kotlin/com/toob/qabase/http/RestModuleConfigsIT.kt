package com.toob.qabase.http

import com.toob.qabase.QaBaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@QaBaseTest
class RestModuleConfigsIT {

    @Autowired
    lateinit var restModuleConfigs: RestModuleConfigs

    @Test
    fun `Should Load Confiurations`() {
        assertNotNull(restModuleConfigs)
        assertNotNull( restModuleConfigs.baseUrl)
        assertEquals("https://jsonplaceholder.typicode.com", restModuleConfigs.baseUrl)
    }
}