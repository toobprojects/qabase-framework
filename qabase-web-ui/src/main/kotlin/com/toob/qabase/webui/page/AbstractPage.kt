package com.toob.qabase.webui.page

/**
 * Base class for all Page Objects in the QA Base Web UI module.
 *
 * This class enforces the implementation of the [VisiblePage] interface and provides
 * access to a [PageFactory] instance for retrieving page instances.
 *
 * @property pageFactory Provides new page objects during navigation.
 */
abstract class AbstractPage(protected val pageFactory: PageFactory) : VisiblePage {
}