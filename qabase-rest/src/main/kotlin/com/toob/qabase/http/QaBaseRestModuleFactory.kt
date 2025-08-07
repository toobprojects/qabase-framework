package com.toob.qabase.http

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan( basePackageClasses = [QaBaseRestPackage::class] )
@EnableConfigurationProperties(RestModuleConfigs::class)
class QaBaseRestModuleFactory