package com.toob.qabase.webui

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.logevents.SelenideLogger
import io.qameta.allure.selenide.AllureSelenide
import jakarta.annotation.PostConstruct
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan


/**
 * Spring Boot configuration entry point for the QA Base Web UI module.
 *
 * This class configures Selenide (browser, timeout, window size, headless mode)
 * using values from [WebUIConfigs] and registers the AllureSelenide listener
 * for Allure reporting of UI tests.
 */

// Marks this as a Spring Boot configuration class.
@SpringBootConfiguration
// Enables auto-configuration for the module.
@EnableAutoConfiguration
// Ensures components in the Web UI package are discovered.
@ComponentScan(basePackageClasses = [QaBaseWebUIPackage::class])
// Binds WebUIConfigs properties.
@EnableConfigurationProperties(WebUIConfigs::class)
class QaBaseWebUIFactory(private val webUIConfigs: WebUIConfigs) {

    /**
     * Configures Selenide with browser, timeout, window size, and headless mode
     * based on [WebUIConfigs]. Registers the AllureSelenide listener to enable
     * detailed UI test reporting with screenshots and page sources in Allure.
     */
    @PostConstruct
    fun configureSelenideAndAllure() {
        // Set Selenide browser type (e.g., chrome, firefox) from configuration
        Configuration.browser = webUIConfigs.browser
        // Set Selenide timeout value from configuration
        Configuration.timeout = webUIConfigs.timeout
        // Set Selenide browser window size from configuration
        Configuration.browserSize = webUIConfigs.browseWindowSize
        // Set Selenide headless mode from configuration
        Configuration.headless = webUIConfigs.headless

        // Register AllureSelenide listener for enhanced Allure reporting with screenshots and page sources
        SelenideLogger.addListener(
            "AllureSelenide",
            AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
                .includeSelenideSteps(true)
        )
    }
}