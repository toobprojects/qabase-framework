package com.toob.qabase.core

import com.toob.qabase.util.FileOps
import com.toob.qabase.util.QALogger
import com.toob.qabase.util.ReportCompressor
import io.qameta.allure.Allure
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


private const val EXT_JSON = "json"
private const val EXT_TEXT = "txt"

/**
 * This Kotlin object provides utility functions for ...
 * ...integrating Allure reporting into your API test automation framework.
 */
object AllureExtensions {

    private val log = QALogger.get(this::class.java)
    private val REPORTS_TIME_STAMP_FORMAT = SimpleDateFormat("yyyy-MM-dd-HHmmss")
    private const val ALLURE_REPORTS_DIR_NAME = "allure-reports"

    /**
     * This function wraps a test step inside an Allure report entry.
     * It ensures that each test action is logged as a step in the Allure report.
     * It converts the action() into an Allure.ThrowableRunnable, which captures exceptions that occur inside the step.
     * It then calls Allure.step(...), passing the description and the wrapped action.
     *
     * @param description : String → A textual description of the step.
     * @param action: () -> T → A lambda function representing the step.
     */
    @JvmStatic
    fun <T> step(description: String, action: () -> T): T {

        // Wrap the provided action inside an Allure ThrowableRunnable (captures errors)
        val throwableRunnable = Allure.ThrowableRunnable { action() }

        // Log the step description and execute the action
        return Allure.step( description, throwableRunnable)
    }


    /**
     * Adds TEXT attachment to Allure Reports.
     * @param title : String → Title of the attachment
     * @param body: () -> T → Description of the attachment
     */
    @JvmStatic
    fun attachText( title: String, body: String) =
        Allure.addAttachment(
            title,   // Attachment title in the report
            "text/plain",  // Specify TEXT format
            body,   // Body of the Attachment
            EXT_TEXT // File extension type
        )

    /**
     * Adds JSON attachment to Allure Reports.
     * @param title : String → Title of the attachment
     * @param body: () -> T → Description of the attachment
     */
    @JvmStatic
    fun attachJson( title: String, body: String) =
        Allure.addAttachment(
            title,   // Attachment title in the report
            "application/json",  // Specify JSON format
            body,   // Body of the Attachment
            EXT_JSON // File extension type
        )


    /**
     * Mainly used to Zip the "allure-reports" folder when tests are running.
     * This will help QA Engineers take a snapshot when done running tests.
     * @param targetDir : File → Destination of where you want to ZIP the file to.
     * Will default to your DOWNLOADS folder when not specified.
     */
    @JvmStatic
    fun zipReportsTo(targetDir: File = FileOps.downloads()) {
        val allureDir = File(ALLURE_REPORTS_DIR_NAME)

        // First make sure the "allure-reports" exists.
        if (!allureDir.exists() || !allureDir.isDirectory) {
            log.error("⚠️ $ALLURE_REPORTS_DIR_NAME folder not found in: ${allureDir.absolutePath}")
            return
        }

        // Create the target directory if it does not exist.
        if (!targetDir.exists()) targetDir.mkdirs()

        // Get ready to Compress
        val archiveName = "allure-${REPORTS_TIME_STAMP_FORMAT.format(Date())}.zip"
        val output = File(targetDir, archiveName)

        // Finally compress
        ReportCompressor.zipDirectory(allureDir, output)
        log.info("✅ Allure report zipped to: ${output.absolutePath}")
    }

}