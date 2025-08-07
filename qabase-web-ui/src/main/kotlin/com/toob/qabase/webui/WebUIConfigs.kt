package com.toob.qabase.webui

import com.toob.qabase.webui.WebUIConstants.WEB_APPLICATION
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = WEB_APPLICATION)
class WebUIConfigs {
    lateinit var baseUrl: String
    lateinit var browser: String
    lateinit var browseWindowSize: String
    var timeout: Long = 0
    var headless: Boolean = false
}