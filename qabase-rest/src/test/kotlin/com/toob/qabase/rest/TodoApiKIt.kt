package com.toob.qabase.rest

import com.toob.qabase.rest.client.RestClient
import com.toob.qabase.rest.model.Todo
import com.toob.qabase.rest.support.HttpSupport
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import io.restassured.common.mapper.TypeRef
import kotlin.test.Test
import org.junit.jupiter.api.DisplayName
import kotlin.test.assertTrue

@Epic("JSONPlaceholder API Tests - Kotlin")
@Feature("Fetch All & Create One TODOs API")
class TodoApiKIt : AbstractRestTest() {

	companion object {
		private val TASK_LIST_TYPE = object : TypeRef<List<Todo>>() {}
	}

	@Test
    @DisplayName("GET /todos returns a non-empty list")
    @Story("Fetch all TODO items")
    fun `Fetch All Tasks`() {
		val todos = HttpSupport.expect(RestClient.get("/todos"))
            .ok()
            .contentType()        // defaults to application/json
            .timeUnder(2_000L)
            .attach().`as`(TASK_LIST_TYPE)

        // Terminal operation: deserialize and assert size using Kotlin
        assertTrue(todos.isNotEmpty(), "Expected at least one TODO item")
    }

	@Test
    @DisplayName("POST /todos creates a TODO and echoes fields")
    @Story("Create a new TODO item")
    fun `Create New Task`() {
        val todo = Todo(
			userId = 1,
			title = "Implement Allure Reports with Kotlin and Spring Boot",
			completed = false
		)

        val requestBody = HttpSupport.toPrettyJson(todo)
        HttpSupport.expect(RestClient.post("/todos", requestBody))
            .created()
            .contentType()      // defaults to application/json
            .timeUnder(2_000L)
            .fieldEq("title", todo.title)
            .fieldEq("completed", todo.completed)
            .fieldEq("userId", todo.userId)
            .attach()
    }
}