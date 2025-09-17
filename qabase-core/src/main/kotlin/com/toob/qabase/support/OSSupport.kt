package com.toob.qabase.support

import com.toob.qabase.util.logger
import java.io.File
import kotlin.system.exitProcess

/**
 * Lightweight OS utilities for QABase.
 *
 * Usage examples:
 *  - OSSupport.isWindows()
 *  - OSSupport.isMac()
 *  - OSSupport.isLinux()
 *  - OSSupport.info()
 *  - OSSupport.isSafari("Safari") // true
 *  - OSSupport.isBrowserSupported("safari") // false (we disallow Safari explicitly)
 */
object OSSupport {

	private val log = logger()

    enum class OSType { WINDOWS, MAC, LINUX, OTHER }

    data class OSInfo(
        val type: OSType,
        val name: String,
        val version: String,
        val arch: String,
        val distro: String? = null
    )

    private val osNameRaw: String = System.getProperty("os.name") ?: "unknown"
    private val osNameLc: String = osNameRaw.lowercase()
    private val osVersion: String = System.getProperty("os.version") ?: "unknown"
    private val osArch: String = System.getProperty("os.arch") ?: "unknown"

    /** Detect the broad OS family. */
    val type: OSType = when {
        osNameLc.contains("win") -> OSType.WINDOWS
        osNameLc.contains("mac") || osNameLc.contains("darwin") -> OSType.MAC
        osNameLc.contains("nix") || osNameLc.contains("nux") || osNameLc.contains("aix") || osNameLc.contains("linux") -> OSType.LINUX
        else -> OSType.OTHER
    }

    fun isWindows(): Boolean = type == OSType.WINDOWS
    fun isMac(): Boolean = type == OSType.MAC
    fun isLinux(): Boolean = type == OSType.LINUX

    /**
     * Return a human-friendly Linux distribution string, if available.
     * Parses /etc/os-release, falling back to null when unavailable.
     */
    fun detectLinuxDistro(): String? {
        if (!isLinux()) return null
        val file = File("/etc/os-release")
        if (!file.exists()) return null
        return runCatching {
            val pairs = file.readLines()
                .mapNotNull { line ->
                    val trimmed = line.trim()
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) return@mapNotNull null
                    val idx = trimmed.indexOf('=')
                    if (idx <= 0) return@mapNotNull null
                    val key = trimmed.substring(0, idx)
                    var value = trimmed.substring(idx + 1)
                    value = value.trim().trim('"')
                    key to value
                }
                .toMap()
            pairs["PRETTY_NAME"]
                ?: pairs["NAME"]
                ?: pairs["ID"]
        }.getOrNull()
    }

    /** Aggregate OS information. */
    fun info(): OSInfo = OSInfo(
        type = type,
        name = osNameRaw,
        version = osVersion,
        arch = osArch,
        distro = if (isLinux()) detectLinuxDistro() else null
    )

    // --- Lightweight browser helpers for upcoming Web UI checks ---
    private fun normalize(name: String): String = name.trim().lowercase()

    fun isSafari(browserName: String): Boolean = normalize(browserName) == "safari"

    fun isChrome(browserName: String): Boolean {
        val b = normalize(browserName)
        return b == "chrome" || b == "google-chrome" || b == "chromium" || b == "msedge" || b == "edge" // Edge is Chromium-based
    }

    /**
     * Global policy gate: at framework level we explicitly disallow Safari
     * (e.g., to avoid users mapping Safari as a stand-in for Chrome).
     * Extend this later if you need stricter per-OS rules.
     */
    fun isBrowserSupported(browserName: String): Boolean = !isSafari(browserName)

    fun validateBrowserOnOS(browserName: String) {
        if (isSafari(browserName) && !isMac()) {
			log.error{"⚠️  Error: Safari browser is only supported on Mac OS."}
            exitProcess(1)
        }
    }
}