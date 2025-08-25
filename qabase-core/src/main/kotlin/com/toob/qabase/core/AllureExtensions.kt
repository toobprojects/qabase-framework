package com.toob.qabase.core

import com.toob.qabase.util.FileOps
import com.toob.qabase.util.ReportCompressor
import com.toob.qabase.util.logger
import io.qameta.allure.Allure
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.function.Supplier


private const val EXT_JSON = "json"
private const val EXT_TEXT = "txt"
private const val MEDIA_TYPE_JSON = "application/json"
private const val MEDIA_TYPE_TEXT = "text/plain"

/**
 * A centralized utility object for integrating Allure reporting into the API test automation framework.
 * This object provides convenient functions for logging test steps, attaching various types of data
 * (such as text and JSON) to Allure reports, and compressing the generated Allure report directory
 * into a zip archive. It simplifies and standardizes how Allure reporting features are used throughout
 * the test codebase.
 */
object AllureExtensions {

    private val log = logger()
    private val REPORTS_TIME_STAMP_FORMAT = SimpleDateFormat("yyyy-MM-dd-HHmmss")
    private const val ALLURE_RESULTS_DIR_NAME = "allure-results"

    /**
     * Executes a test step within an Allure report entry.
     *
     * This function wraps the given [action] lambda inside an Allure step, ensuring that the step
     * description and any exceptions thrown during the execution are properly recorded in the Allure report.
     * It returns the result of the [action] lambda.
     *
     * @param description A textual description of the step to be displayed in the Allure report.
     * @param action A lambda representing the code block to execute as the step.
     * @return The result of the executed [action].
     */
    @JvmStatic
    fun <T> step( description: String, action: () -> T): T {

        // Wrap the provided action inside an Allure ThrowableRunnable (captures errors)
        val throwableRunnable = Allure.ThrowableRunnable { action() }

        // Log the step description and execute the action
        return Allure.step( description, throwableRunnable)
    }

	/**
	 * Java-friendly overload: allows calling step("desc", () -> value) from Java without Kotlin Function0.
	 */
	@JvmStatic
	fun <T> step( description: String, supplier: Supplier<T>): T {
		return Allure.step(
			description,
			Allure.ThrowableRunnable { supplier.get() }
		)
	}


    /**
     * Attaches plain text content to the Allure report.
     *
     * Useful for including debugging information, logs, or any textual data that helps understand
     * the test execution context.
     *
     * @param title Title of the attachment shown in the Allure report.
     * @param body The plain text content to attach.
     */
    @JvmStatic
    fun attachText( title: String, body: String) {
		body.let {
			Allure.addAttachment(
				title,   // Attachment title in the report
				MEDIA_TYPE_TEXT,  // Specify TEXT format
				body,   // Body of the Attachment
				EXT_TEXT // File extension type
			)
		}
	}


    /**
     * Attaches JSON content to the Allure report.
     *
     * This is helpful for including JSON payloads such as API requests, responses, or other structured data
     * for debugging and traceability.
     *
     * @param title Title of the attachment shown in the Allure report.
     * @param body The JSON string content to attach.
     */
    @JvmStatic
    fun attachJson( title: String, body: String) {
		body.let {
			Allure.addAttachment(
				title,   // Attachment title in the report
				MEDIA_TYPE_JSON,  // Specify JSON format
				body,   // Body of the Attachment
				EXT_JSON // File extension type
			)
		}
	}



    /**
     * Compresses the "allure-reports" directory into a zip archive at the specified [targetDir].
     *
     * This method is intended to snapshot the Allure report output after test execution, making it easier
     * for QA engineers to archive, share, or upload the reports. If [targetDir] is not specified,
     * it defaults to the user's downloads directory.
     *
     * The method logs an error if the "allure-reports" directory does not exist, and logs info upon
     * successful compression including the path to the generated zip file.
     *
     * @param targetDir Destination directory for the zip archive. Defaults to the downloads folder.
     */
    @JvmStatic
    fun zipReportsTo(targetDir: File = FileOps.downloads()) {
        val allureDir = File(ALLURE_RESULTS_DIR_NAME)

        // Check if the "allure-reports" directory exists and is a directory
        if (!allureDir.exists() || !allureDir.isDirectory) {
            log.error{"⚠️ $ALLURE_RESULTS_DIR_NAME folder not found in: ${allureDir.absolutePath}"}
            return
        }

        // Create the target directory if it does not exist
        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }

        // Prepare the output zip file with timestamped name
        val archiveName = "allure-${REPORTS_TIME_STAMP_FORMAT.format(Date())}.zip"
        val output = File(targetDir, archiveName)

        // Compress the allure-reports directory into the output zip file
        ReportCompressor.zipDirectory(allureDir, output)
        log.info{"✅ Allure report zipped to: ${output.absolutePath}"}
    }

}