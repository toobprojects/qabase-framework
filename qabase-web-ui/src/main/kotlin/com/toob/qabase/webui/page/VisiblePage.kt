package com.toob.qabase.webui.page

/**
 * Contract for all page objects to implement a visibility check.
 */
interface VisiblePage {
	/**
	 * Verifies the current page is displayed/visible.
	 * @return the page instance for fluent chaining.
	 */
	fun verifyVisible(): VisiblePage
}