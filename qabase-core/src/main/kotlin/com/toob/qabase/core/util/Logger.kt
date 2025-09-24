package com.toob.qabase.core.util

import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * Global logger utility that allows any class to easily log messages.
 * Usage: `val logger = logger()` inside any Kotlin file.
 */
inline fun <reified T> T.logger() =
	KotlinLogging.logger(T::class.qualifiedName ?: "UnknownLogger")