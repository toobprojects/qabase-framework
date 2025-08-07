package com.toob.qabase.util

import java.io.File


object FileOps {

	const val TARGET_DIR = "target"
	const val ALLURE_REPORTS = "allure-results"
	private const val USER_HOME = "user.home"
	private const val DOWNLOADS_DIR = "Downloads"

	/**
	 * Generic function to Derive your machine Downloads Folder.
	 */
	fun downloads(): File {
		val path = File(System.getProperty(USER_HOME), DOWNLOADS_DIR)

		// Created the downlaods folder if you don't have it, though you should have one standard
		if (!path.exists()) path.mkdirs()
		return path
	}

}