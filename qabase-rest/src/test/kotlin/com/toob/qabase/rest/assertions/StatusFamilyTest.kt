package com.toob.qabase.rest.assertions


import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StatusFamilyTest {

	@Test
	fun `Contains - Happy Paths`() {
		assertTrue(StatusFamily.SUCCESS.contains(200))
		assertTrue(StatusFamily.CLIENT_ERROR.contains(404))
		assertTrue(StatusFamily.SERVER_ERROR.contains(500))
	}

	@Test
	fun `Contains - Negatives`() {
		assertFalse(StatusFamily.SUCCESS.contains(199))
		assertFalse(StatusFamily.SERVER_ERROR.contains(499))
	}

	@Test
	fun `Of - Maps Exact Codes`() {
		assertEquals(StatusFamily.INFORMATIONAL, StatusFamily.of(101))
		assertEquals(StatusFamily.SUCCESS, StatusFamily.of(204))
		assertEquals(StatusFamily.REDIRECTION, StatusFamily.of(301))
		assertEquals(StatusFamily.CLIENT_ERROR, StatusFamily.of(422))
		assertEquals(StatusFamily.SERVER_ERROR, StatusFamily.of(503))
	}
}