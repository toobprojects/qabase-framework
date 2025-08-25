package com.toob.qabase.webui.pages

/** Contract for page visibility with self-typed return for fluent chaining. */
interface VisiblePage<T : VisiblePage<T>> {
	/**
	 * Verifies the current page is displayed/visible and returns the page instance.
	 */
	fun verifyVisible(): T
}