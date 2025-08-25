package com.toob.qabase.rest

import com.toob.qabase.core.AllureExtensions.step
import com.toob.qabase.rest.client.RestClient
import com.toob.qabase.rest.model.Todo
import com.toob.qabase.rest.support.HttpSupport
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.hamcrest.Matchers.greaterThan
import kotlin.test.Test

@Epic("JSONPlaceholder API Tests - Kotlin")
@Feature("Fetch All & Create One TODOs API")
class TodoApiKIt : AbstractRestTest() {

    @Test
    @Story("Fetch all TODO items")
    fun `Fetch All Tasks`() {
        val response = step("Fetch All TODO Tasks") {
            RestClient.get("/todos")
        }

        HttpSupport.allOkay(response)
        step("Validate response contains at least one TODO item") {
            response.then().body("size()", greaterThan(0))
        }
    }

    @Test
    @Story("Create a new TODO item")
    fun `Create New Task`() {
        val todo = Todo(userId = 1, title = "Implement Allure Reports with Kotlin and Spring Boot", completed = false)
        val requestBody = jsonConfig.encodeToString(todo)

        val response = step("Creating a new TODO task") {
            RestClient.post("/todos", requestBody)
        }

        HttpSupport.created(response)
    }

    @Test
    @Story("Update an existing TODO item")
    fun `Update Task`() {
        val todo = Todo(userId = 1, title = "Updated Task", completed = false)
        val requestBody = jsonConfig.encodeToString(todo)

        val response = step("Updating a TODO") {
            RestClient.put("/todos/1", requestBody)
        }

        HttpSupport.allOkay( response)
    }

    @Test
    @Story("Delete a TODO item")
    fun `Delete Task`() {
        val response = step("Removing a TODO") {
            RestClient.delete("/todos/1")
        }

        HttpSupport.allOkay( response)
    }
}