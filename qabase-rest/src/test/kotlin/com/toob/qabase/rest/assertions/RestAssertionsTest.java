package com.toob.qabase.rest.assertions;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RestAssertionsTest {

    @Test
    @DisplayName("Should call static facade from Java")
    void shouldCallStaticFacadeFromJava() {
        Response resp = jsonResponse();
        RestAssertions.expect(resp)
                .ok()
                .contentType(ContentType.JSON.toString())
                .fieldEq("id", 1);
    }

    private Response jsonResponse() {
        return new ResponseBuilder()
                .setStatusCode(200)
                .setContentType(ContentType.JSON.toString())
                .setBody("{\"id\":1}")
                .build();
    }
}
