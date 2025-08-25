package com.toob.qabase.webui.pages

import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 * Spring-aware factory for resolving Page Objects as beans.
 * Keep pages stateless and annotate with @Component (singleton is fine).
 */
@Component
class PageFactory(private val context: ApplicationContext) {

	/** DSL-style generic version of get() using reified type (Kotlin-friendly). */
	fun <T : Any> get(clazz: KClass<T>): T = context.getBean(clazz.java)

	/** Get a Spring-managed page object using a class reference. */
	fun <T : Any> get(pageClass: Class<T>): T = context.getBean(pageClass)

}