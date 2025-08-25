package com.toob.qabase.rest;

import com.toob.qabase.rest.model.Todo;
import com.toob.qabase.rest.support.HttpSupport;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.toob.qabase.core.AllureExtensions.step;
import static com.toob.qabase.rest.RestModuleConstants.DEFAULT_CONTENT_TYPE;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;

@Epic("JSONPlaceholder API Tests - Java")
@Feature("Fetch For Update, Then Delete")
public class TodoApiJavaIT extends AbstractRestTest {

    private static final int TODO_ID_TO_UPDATE = 3;
    private static final int TODO_ID_TO_DELETE = 2;

    @Test
    @SneakyThrows
    @DisplayName("PUT /todos/{id} should echo updated fields")
    @Story("Update an existing TODO item")
    void updateTask() {

        // Fetch a Task By Id
        // Verify the response
        Response taskResponse = step("Fetch a Task By Id", TodoFunctions.fetchById(TODO_ID_TO_UPDATE));
        HttpSupport.allOkay(taskResponse);
        HttpSupport.expect(taskResponse)
                .fieldEq("id", 3)
                .fieldEq("title", "fugiat veniam minus")
                .fieldEq("completed", false)
                .contentType(DEFAULT_CONTENT_TYPE)
                .timeUnder(2_000L);

        // Attach response to Allure report
        HttpSupport.attachResponse(taskResponse);

        // Update the Task
        Todo task = taskResponse.as(Todo.class);
        Todo updated = task.copy(null, task.getUserId(), "Updated Task", true);
        Response updateResponse = step("Update Task #3 status via PUT", TodoFunctions.updateById(TODO_ID_TO_UPDATE, updated));
        HttpSupport.allOkay(updateResponse);

        // Attach response to Allure report
        HttpSupport.attachResponse(updateResponse);
        Todo updatedTask = updateResponse.as(Todo.class);
        assertAll(
                () -> assertNotNull(updatedTask),
                () -> assertEquals(3, updatedTask.getId()),
                () -> assertEquals("Updated Task", updatedTask.getTitle()),
                () -> assertTrue(updatedTask.getCompleted())
        );

    }

    @Test
    @DisplayName("DELETE /todos/{id} removes a TODO successfully")
    @Story("Delete a TODO item")
    void deleteTask() {
        // Delete a Task
        Response deleteResp = step("Removing a Task", TodoFunctions.deleteById(TODO_ID_TO_DELETE));
        HttpSupport.allOkay(deleteResp);
        HttpSupport.attachResponse(deleteResp);
        deleteResp.then().body("size()", lessThanOrEqualTo(1));
    }

}