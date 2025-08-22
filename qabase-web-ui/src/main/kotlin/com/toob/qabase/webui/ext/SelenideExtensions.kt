package com.toob.qabase.webui.ext

import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.`$$`
import com.codeborne.selenide.Selenide.open
import org.openqa.selenium.By

/**
 * Utility object providing common static helpers for working with Selenide in web UI tests.
 *
 * The functions in this object make it easy to open the browser, locate elements by name, id, or CSS selector,
 * and retrieve collections of elements. All functions are static and designed for concise, readable test code.
 */
object SelenideExtensions {

    /**
     * Opens the browser at the base URL ("/").
     *
     * @return the opened page object
     */
    @JvmStatic
    fun openBrowser() = open("/")

    /**
     * Finds a web element by its `name` attribute.
     *
     * @param name the value of the `name` attribute to search for
     * @return the found Selenide element
     */
    @JvmStatic
    fun byName(name : String) = `$`(By.name(name))

    /**
     * Finds a web element by its `id` attribute.
     *
     * @param id the value of the `id` attribute to search for
     * @return the found Selenide element
     */
    @JvmStatic
    fun byId(id : String) = `$`(By.id(id))

    /**
     * Finds a web element using a CSS selector.
     *
     * @param link the CSS selector string
     * @return the found Selenide element
     */
    @JvmStatic
    fun byCss(link : String) = `$`(link)

    /**
     * Finds a collection of web elements by name or id selector.
     *
     * @param nameOrId the selector string (e.g., "[name='foo']" or "#bar")
     * @return the found Selenide element collection
     */
    @JvmStatic
    fun eleCollection(nameOrId : String) = `$$`(nameOrId)
}

