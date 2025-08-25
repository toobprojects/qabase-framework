package com.toob.qabase.rest.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Todo(
    val id: Int? = null,
    val userId: Int,
    val title: String,
    val completed: Boolean
)