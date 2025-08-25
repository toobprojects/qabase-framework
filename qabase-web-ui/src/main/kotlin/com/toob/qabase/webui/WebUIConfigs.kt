package com.toob.qabase.webui

import com.toob.qabase.webui.WebUIConstants.WEB_APPLICATION
import org.springframework.boot.context.properties.ConfigurationProperties


/**
 * Holds configuration properties for Web UI tests.
 *
 * This class is bound to properties prefixed with [WEB_APPLICATION] in the application properties.
 * It provides settings such as the base URL, browser, window size, timeout, and headless mode for test execution.
 */
@ConfigurationProperties(prefix = WEB_APPLICATION)
data class WebUIConfigs (
    /**
     * Represents the base URL of the application under test.
     */
	var baseUrl: String = "",

    /**
     * Defines the browser to be used for tests (e.g., Chrome, Firefox).
     */
	var browser: String = "chrome",

    /**
     * Sets the default browser window size (e.g., 1920x1080).
     */
	var browseWindowSize: String = "1920x1080",

    /**
     * Defines the global timeout for waiting on elements in seconds.
     */
	var timeout: Long = 4000,

    /**
     * Controls whether the browser should run in headless mode (true) or with a UI (false).
     */
	var headless: Boolean = false
)