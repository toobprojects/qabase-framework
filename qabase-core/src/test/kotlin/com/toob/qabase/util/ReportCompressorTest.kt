package com.toob.qabase.util

import com.toob.qabase.core.AbstractQABaseTest
import com.toob.qabase.core.util.FileOps
import com.toob.qabase.core.util.ReportCompressor
import com.toob.qabase.core.util.logger
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import java.io.File
import java.nio.file.Files
import kotlin.test.Test

class ReportCompressorTest : AbstractQABaseTest() {

	private val log = logger()
	private lateinit var tempDir: File
	private lateinit var dummyReportDir: File

	@BeforeAll
	fun setup() {
		// Create a temporary allure-reports directory with dummy files
		tempDir = Files.createTempDirectory("qabase-temp").toFile()
		dummyReportDir = File(tempDir, FileOps.ALLURE_REPORTS)
			.apply { mkdirs() }

		// Create dummy report files
		File(dummyReportDir, "index.html").writeText("<html><body>Report</body></html>")
		File(dummyReportDir, "summary.json").writeText("{ \"status\": \"passed\" }")
	}

	@AfterAll
	fun cleanup() {
		tempDir.deleteRecursively()
	}

	@Test
	fun `Should Zip allure-reports To Downloads folder`() {
		val zipFile = File(File(FileOps.TARGET_DIR), "allure-${System.currentTimeMillis()}.zip")
		ReportCompressor.zipDirectory(dummyReportDir, zipFile)

		assertTrue(zipFile.exists(), "ZIP file should be created in Downloads folder")
		log.info{"✅ Zipped file: ${zipFile.absolutePath}"}
	}

	@Test
	fun `Should TarGz allure-reports To Downloads Folder`() {
		val tarGzFile = File(File(FileOps.TARGET_DIR), "allure-${System.currentTimeMillis()}.tar.gz")
		ReportCompressor.tarGzipDirectory(dummyReportDir, tarGzFile)

		assertTrue(tarGzFile.exists(), "TAR.GZ file should be created in Downloads folder")
		log.info{"✅ TAR.GZ file: ${tarGzFile.absolutePath}"}
	}

}