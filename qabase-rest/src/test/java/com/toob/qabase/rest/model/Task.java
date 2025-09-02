package com.toob.qabase.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Test-only DTO for GoRest /Tasks payloads.
 * Keeping it here avoids cross-module coupling just for tests.
 */
public class Task {

    // nullable in Kotlin -> use wrapper type
    private Integer id;

    // Kotlin Int -> Java primitive int
    @JsonProperty("userId")
    private int userId;

    private String title;

    private boolean completed;

    /** No-args constructor required by some serializers/deserializers */
    public Task() { }

    public Task(Integer id, int userId, String title, boolean completed) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.completed = completed;
    }

    // Getters & Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("userId")
    public int getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                '}';
    }

}
