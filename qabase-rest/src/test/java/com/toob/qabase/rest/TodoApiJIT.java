package com.toob.qabase.rest;

import com.toob.qabase.rest.client.RestClient;
import com.toob.qabase.rest.model.Todo;
import com.toob.qabase.rest.support.HttpSupport;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import kotlin.jvm.functions.Function0;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static com.toob.qabase.core.AllureExtensions.step;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Epic("JSONPlaceholder API Tests - Java")
@Feature("Fetch For Update, Then Delete")
public class TodoApiJIT extends AbstractRestTest {

    @Test
    @Story("Fetch & Update A Task")
    void updateTask() {

        // Fetch a Task By Id
        // Verify the response
        Response taskResponse = step("Fetch a Task By Id", fetchById.apply(3));
        HttpSupport.allOkay(taskResponse);
        taskResponse.then()
                .assertThat()
                .body("id", equalTo(3))
                .body("title", equalTo("fugiat veniam minus"))
                .body("completed", equalTo(false));

        // Update the Task
        Todo task = taskResponse.as(Todo.class);
        Todo updated = task.copy(null, task.getUserId(), "Updated Task", true);
        Response updateResponse = step("Update Task #3 status via PUT", updateById.apply(updated));
        HttpSupport.allOkay(updateResponse);
        Todo updatedTask = updateResponse.as(Todo.class);
        assertNotNull(updatedTask);
        assertEquals(3, updatedTask.getId());
        assertEquals("Updated Task", updatedTask.getTitle());
        assertTrue( updatedTask.getCompleted());
    }

    @Test
    @Story("Delete a TODO item")
    void deleteTask() {
        // Delete a Task
        Response response = step("Removing a Task", deleteById.apply(2));
        HttpSupport.allOkay(response);
    }


    private static final Function<Integer, Function0<Response>> fetchById =
            taskId -> () -> RestClient.get("/todos/" + taskId);

    private static final Function<Todo, Function0<Response>> updateById =
            (task) -> () -> RestClient.put("/todos/" + task.getId(), task);

    private static final Function<Integer, Function0<Response>> deleteById =
            (taskId) -> () -> RestClient.delete("/todos/" + taskId);
}