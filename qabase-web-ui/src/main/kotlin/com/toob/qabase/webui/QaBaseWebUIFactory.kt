package com.toob.qabase.webui

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.logevents.SelenideLogger
import io.qameta.allure.selenide.AllureSelenide
import jakarta.annotation.PostConstruct
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan


@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan( basePackageClasses = [QaBaseWebUIPackage::class] )
@EnableConfigurationProperties(WebUIConfigs::class)
class QaBaseWebUIFactory( private val webUIConfigs: WebUIConfigs ) {

	@PostConstruct
	fun configureSelenideAndAllure() {
		Configuration.browser = webUIConfigs.browser
		Configuration.timeout = webUIConfigs.timeout
		Configuration.browserSize = webUIConfigs.browseWindowSize
		Configuration.headless = webUIConfigs.headless

		SelenideLogger.addListener(
			"AllureSelenide",
			AllureSelenide()
				.screenshots(true)
				.savePageSource(true)
				.includeSelenideSteps(true)
		)
	}
}