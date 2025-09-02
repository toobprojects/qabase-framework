package com.toob.qabase.rest;

import com.toob.qabase.rest.client.RestClient;
import com.toob.qabase.rest.model.Task;
import io.restassured.response.Response;

import java.util.function.Supplier;

/**
 * Utility functions for interacting with the JSONPlaceholder Task API.
 * Keeps HTTP plumbing separate from test assertions.
 */
public class TodoFunctions {

    public static final String TODOS_PATH = "/todos/";

    private TodoFunctions() {
    } // utility holder

    /** Fetch a Task by id */
    public static Supplier<Response> fetchById(int taskId) {
        return () -> RestClient.get(TODOS_PATH + taskId);
    }

    /** Delete a Task by id */
    public static Supplier<Response> deleteById(int taskId) {
        return () -> RestClient.delete(TODOS_PATH + taskId);
    }

    /** Update a Task by id with the given payload */
    public static Supplier<Response> updateById(int taskId, Task task) {
        return () -> RestClient.put(TODOS_PATH + taskId, task);
    }

}
