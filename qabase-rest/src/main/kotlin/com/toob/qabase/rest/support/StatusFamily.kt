package com.toob.qabase.rest.support

/**
 * HTTP status code families per RFC (1xx, 2xx, 3xx, 4xx, 5xx).
 * Java-friendly helpers included.
 */
enum class StatusFamily(val range: IntRange) {
	INFORMATIONAL(100..199),
	SUCCESS(200..299),
	REDIRECTION(300..399),
	CLIENT_ERROR(400..499),
	SERVER_ERROR(500..599);

	/** True if the given status code falls in this family. */
	fun contains(status: Int): Boolean = status in range

	companion object {
		/** Determine the family for a specific status code. */
		@JvmStatic
		fun of(status: Int): StatusFamily =
			values().first { status in it.range }
	}
}