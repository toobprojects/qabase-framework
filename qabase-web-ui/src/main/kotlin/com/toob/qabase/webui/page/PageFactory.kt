package com.toob.qabase.webui.page

import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

/**
 * Generic Spring-aware factory for resolving Page Object components.
 * This factory uses Spring's ApplicationContext to dynamically retrieve any Page Object bean.
 *
 * Usage:
 *    val loginPage = pageFactory.get<LoginPage>()
 */
@Component
final class PageFactory(private val context: ApplicationContext) {

	/**
	 * Get a Spring-managed page object using a class reference.
	 */
	fun <T : Any> get(pageClass: Class<T>): T = context.getBean(pageClass)

	/**
	 * DSL-style generic version of get() using reified type.
	 * Usage: pageFactory.get<HomePage>()
	 */
	inline fun <reified T : Any> get(): T = get(T::class.java)
}