package com.toob.qabase.http.model

import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val id: Int? = null,
    val userId: Int,
    val title: String,
    val completed: Boolean
)