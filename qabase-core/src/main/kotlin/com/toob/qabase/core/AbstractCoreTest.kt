package com.toob.qabase.core

import org.junit.jupiter.api.TestInstance

// Allows non-static @BeforeAll
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractCoreTest