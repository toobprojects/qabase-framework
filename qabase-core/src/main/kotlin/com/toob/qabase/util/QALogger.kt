package com.toob.qabase.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object QALogger {
	@JvmStatic
	fun get(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)
}