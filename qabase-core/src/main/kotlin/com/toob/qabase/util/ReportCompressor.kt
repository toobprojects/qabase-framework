package com.toob.qabase.util

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ReportCompressor {

	private val log = logger()
	private val TIME_STAMP_FORMAT = SimpleDateFormat("yyyy-MM-dd-HHmmss")

	// === Utility Method 1: Standard ZIP (Good for Windows users, IDEs, etc.) ===
	fun zipDirectory(sourceDir: File, outputZip: File) {
		if (!sourceDir.exists() || !sourceDir.isDirectory) {
			log.error{"[ZIP] Source folder does not exist: ${sourceDir.absolutePath}"}
			return
		}

		// Create a stream that writes compressed ZIP data to the given file
		ZipOutputStream(FileOutputStream(outputZip)).use { zipOut ->
			// Within the .use{} ...
			// we open this resource, use it, and close it safely when done
			// Even if errors occur
			zipFolder(sourceDir, sourceDir, zipOut)
		}
		log.info{"[ZIP] Folder zipped to: ${outputZip.absolutePath}"}
	}

	private fun zipFolder(rootDir: File, currentFile: File, zipOut: ZipOutputStream) {
		if (currentFile.isDirectory) {
			// List all the files in the directory
			// Applying the recurssion algorithm here to zip in
			currentFile.listFiles()?.forEach { file ->
				zipFolder(rootDir, file, zipOut)
			}
		} else {
			val entryName = rootDir.toPath().relativize(currentFile.toPath()).toString()
			zipOut.putNextEntry(ZipEntry(entryName))
			FileInputStream(currentFile).use { it.copyTo(zipOut) }
			zipOut.closeEntry()
		}
	}

	// === Utility Method 2: TAR.GZ (Best for Linux/macOS and CI tools like Jenkins) ===
	fun tarGzipDirectory(sourceDir: File, outputTarGz: File) {
		if (!sourceDir.exists() || !sourceDir.isDirectory) {
			log.error{"[TAR.GZ] Source folder does not exist: ${sourceDir.absolutePath}"}
			return
		}

		FileOutputStream(outputTarGz).use { fos ->
			BufferedOutputStream(fos).use { bos ->
				GzipCompressorOutputStream(bos).use { gzos ->
					TarArchiveOutputStream(gzos).use { taos ->
						taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU)
						addFilesToTarGz(sourceDir, "", taos)
					}
				}
			}
		}
		log.info{"[TAR.GZ] Folder compressed to: ${outputTarGz.absolutePath}"}
	}

	private fun addFilesToTarGz(file: File, parent: String, taos: TarArchiveOutputStream) {
		val entryName = if (parent.isEmpty()) file.name else "$parent/${file.name}"
		val entry = TarArchiveEntry(file, entryName)

		taos.putArchiveEntry(entry)

		if (file.isFile) {
			FileInputStream(file).use { fis -> fis.copyTo(taos) }
			taos.closeArchiveEntry()
		} else {
			taos.closeArchiveEntry()
			file.listFiles()?.forEach { child ->
				addFilesToTarGz(child, entryName, taos)
			}
		}
	}

	// === Optional convenience method to generate filename with timestamp ===
	fun defaultZipFileName(): String = "allure-${TIME_STAMP_FORMAT.format(Date())}.zip"
	fun defaultTarGzFileName(): String = "allure-${TIME_STAMP_FORMAT.format(Date())}.tar.gz"

}