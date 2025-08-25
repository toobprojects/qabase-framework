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
    public static final long SERVICE_LEVEL_AGREEMENT_RESPONSE_TIME_THRESHOLD = 2_000L;

    @Test
    @SneakyThrows
    @DisplayName("PUT /todos/{id} should echo updated fields")
    @Story("Update an existing TODO item")
    void updateTask() {

        // --- Fetch & verify a Task by ID ---
        Response taskResponse = step("Fetch a Task By Id", TodoFunctions.fetchById(TODO_ID_TO_UPDATE));
        Todo task = HttpSupport.expect(taskResponse)
                .ok()
                .fieldEq("id", 3)
                .fieldEq("title", "fugiat veniam minus")
                .fieldEq("completed", false)
                .contentType(DEFAULT_CONTENT_TYPE)
                .timeUnder(SERVICE_LEVEL_AGREEMENT_RESPONSE_TIME_THRESHOLD)
                .attach() // attaches request/response to Allure Reports
                .as(Todo.class);

        // --- Update the Task & verify response ---
        Todo updated = task.copy(null, task.getUserId(), "Updated Task", true);
        Response updateResponse = step("Update Task #3 status via PUT",
                TodoFunctions.updateById(TODO_ID_TO_UPDATE, updated));
        Todo updatedTask = HttpSupport.expect(updateResponse)
                .ok()
                .contentType(DEFAULT_CONTENT_TYPE)
                .timeUnder(SERVICE_LEVEL_AGREEMENT_RESPONSE_TIME_THRESHOLD)
                .attach()
                .as(Todo.class); // attaches request/response to Allure Reports

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
        // --- Delete Task and verify response ---
        Response deleteResp = step("Removing a Task", TodoFunctions.deleteById(TODO_ID_TO_DELETE));

        // Showcase: QABase fluent assertions (Java-friendly)
        HttpSupport.expect(deleteResp)
                .ok()
                .emptyOrSizeAtMost(1)  // JSONPlaceholder often returns {} or [] on delete
                .contentType(DEFAULT_CONTENT_TYPE)  // prove JSON round-trip
                .timeUnder(2_000L);
    }

}