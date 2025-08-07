package com.toob.qabase.webui.ext

import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.`$$`
import com.codeborne.selenide.Selenide.open
import org.openqa.selenium.By

object SelenideExtensions {


    @JvmStatic
    fun openBrowser() = open("/")

    @JvmStatic
    fun byName(name : String) = `$`(By.name(name))

    @JvmStatic
    fun byId(id : String) = `$`(By.id(id))

    @JvmStatic
    fun byCss(link : String) = `$`(link)

    @JvmStatic
    fun eleCollection(nameOrId : String) = `$$`(nameOrId)
}

