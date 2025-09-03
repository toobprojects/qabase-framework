package com.toob.qabase.core

import com.toob.qabase.CoreModuleConstants
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

		return if (allureEnabled() && allureLifecycleActive()) {
			// Wrap the provided action inside an Allure ThrowableRunnable (captures errors)
			val throwableRunnable = Allure.ThrowableRunnable { action() }

			// Log the step description and execute the action
			return Allure.step( description, throwableRunnable)
		} else {
			action()
		}

    }

	/**
	 * Java-friendly overload: allows calling step("desc", () -> value) from Java without Kotlin Function0.
	 */
	@JvmStatic
	fun <T> step( description: String, supplier: Supplier<T>): T {
		return if (allureEnabled() && allureLifecycleActive()) {
			Allure.step(
				description,
				Allure.ThrowableRunnable { supplier.get() }
			)
		} else {
			supplier.get()
		}
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
		if (!allureEnabled() && allureLifecycleActive()) return
		body.let {
			Allure.addAttachment(
				title,                // Attachment title in the report
				MEDIA_TYPE_TEXT,      // Correct media type for text
				body,                 // Attachment body
				EXT_TEXT              // File extension
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
		if (!allureEnabled() && allureLifecycleActive()) return
		body.let {
			Allure.addAttachment(
				title,                // Attachment title in the report
				MEDIA_TYPE_JSON,      // Specify JSON format
				body,                 // Body of the attachment
				EXT_JSON              // File extension type
			)
		}
	}


	/**
	 * Runtime switch for Allure integration.
	 *
	 * This returns `true` when either of these JVM system properties are set in the *test JVM*:
	 * 1. `-Dallure=on` — quick/legacy toggle (CLI friendly)
	 * 2. `-Dqabase.allure.enabled=true` — **preferred** toggle, enabled automatically by the
	 *    `allure-reports` Maven profile via surefire/failsafe `<systemPropertyVariables>`.
	 *
	 * When this method returns `false` (the default), all QABase Allure helpers are **no-ops**:
	 * - `step(...)` executes the block but does not create a report step
	 * - `attachText(...)` / `attachJson(...)` do nothing
	 * - `zipReportsTo(...)` short‑circuits
	 *
	 * This allows us to keep Allure API imports at compile time without producing
	 * `allure-results/` unless the user explicitly opts in.
	 */
	private fun allureEnabled(): Boolean =
		System.getProperty(CoreModuleConstants.PROP_ALLURE_REPORTS) == "true"

	/**
	 * Returns true if an active Allure test case is running in the current thread.
	 * This prevents lifecycle errors when calling Allure helpers outside a test context.
	 */
	private fun allureLifecycleActive(): Boolean =
		runCatching { Allure.getLifecycle().currentTestCase.isPresent }
			.getOrDefault(false)



}