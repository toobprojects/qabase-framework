package com.toob.qabase.core

import com.toob.qabase.util.ReportCompressor
import com.toob.qabase.util.logger
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.io.File


@Component
class ReportArchiverListener {

	companion object {
		private val log = logger()

		private const val ALLURE_REPORTS_DIR = "allure-results"
		private const val TARGET_DIR = "target"
		private const val USER_DIR = "user.dir"
	}

	@EventListener
	fun onContextClosed(event: ContextClosedEvent) {
		val projectRoot = File(System.getProperty(USER_DIR))
		val allureReportFolder = File(projectRoot, ALLURE_REPORTS_DIR)
		val outputZip = File(projectRoot, "${TARGET_DIR}/${ReportCompressor.defaultTarGzFileName()}")

		if (allureReportFolder.exists()) {
			ReportCompressor.tarGzipDirectory(allureReportFolder, outputZip)
			log.info {"✅  Zipped Allure report to : ${outputZip.absolutePath}"}
		} else {
			log.error{"⚠️  No allure-reports folder found at : ${allureReportFolder.absolutePath}"}
		}
	}

}