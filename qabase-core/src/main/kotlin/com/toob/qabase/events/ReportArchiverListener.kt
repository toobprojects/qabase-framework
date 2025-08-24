package com.toob.qabase.events

import com.toob.qabase.util.ReportCompressor
import com.toob.qabase.util.logger
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.io.File

/**
 * Listener that reacts to Spring's ContextClosedEvent to archive Allure test results.
 * Upon context shutdown, it compresses the Allure results directory into a timestamped
 * `.tar.gz` file inside the `target/` directory, using the ReportCompressor utility.
 */
@Component
class ReportArchiverListener {

	companion object {
		/**
		 * Constants representing directory names and system properties:
		 * - ALLURE_REPORTS_DIR: directory where Allure results are stored.
		 * - TARGET_DIR: output directory for the compressed archive.
		 * - USER_DIR: system property key for the current user directory.
		 *
		 * The `log` is an internal logger helper for logging messages.
		 */
		private val log = logger()

		private const val ALLURE_REPORTS_DIR = "allure-results"
		private const val TARGET_DIR = "target"
		private const val USER_DIR = "user.dir"
	}

	/**
	 * Lifecycle hook triggered when the Spring application context is closed.
	 * It resolves the project root directory from the `user.dir` system property,
	 * locates the `allure-results` directory, compresses it into a timestamped
	 * `.tar.gz` file inside the `target/` directory, and logs the success or failure.
	 */
	@EventListener
	fun onContextClosed(event: ContextClosedEvent) {
		// Resolve the project root directory from system property
		val projectRoot = File(System.getProperty(USER_DIR))
		// Compute input Allure results folder and output compressed archive file paths
		val allureReportFolder = File(projectRoot, ALLURE_REPORTS_DIR)
		val outputZip = File(projectRoot, "${TARGET_DIR}/${ReportCompressor.defaultTarGzFileName()}")

		// Check if Allure results folder exists before compressing
		if (allureReportFolder.exists()) {
			// Compress the Allure results directory to the output archive
			ReportCompressor.tarGzipDirectory(allureReportFolder, outputZip)
			// Log success with the output archive path
			log.info {"✅  Zipped Allure report to : ${outputZip.absolutePath}"}
		} else {
			// Log an error if the Allure results directory is not found
			log.error{"⚠️  No allure-reports folder found at : ${allureReportFolder.absolutePath}"}
		}
	}

}