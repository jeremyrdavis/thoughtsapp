package com.redhat.demos.thoughts.admin;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ThoughtCrudTest {

    @Test
    public void testCreateFormReturns200() {
        given()
            .when().get("/thoughts/create")
            .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("Create New Thought"));
    }

    @Test
    public void testCreateThoughtWithValidData() {
        given()
            .formParam("content", "This is a valid thought with enough characters for testing")
            .formParam("author", "Test Author")
            .formParam("authorBio", "Test bio")
            .redirects().follow(false)
            .when().post("/thoughts/create")
            .then()
                .statusCode(303)
                .header("Location", containsString("/thoughts/"));
    }

    @Test
    public void testCreateThoughtWithInvalidData() {
        given()
            .formParam("content", "short")
            .formParam("author", "Test Author")
            .formParam("authorBio", "Test bio")
            .when().post("/thoughts/create")
            .then()
                .statusCode(200)
                .body(containsString("Thought content must be between 10 and 500 characters"));
    }

    @Test
    public void testEditFormReturns200() {
        // Use a UUID from import.sql seed data
        given()
            .when().get("/thoughts/a1b2c3d4-e5f6-7890-abcd-ef1234567890/edit")
            .then()
                .statusCode(200)
                .body(containsString("The only way to do great work is to love what you do"));
    }

    @Test
    public void testDeleteThoughtRedirects() {
        // Use a UUID from import.sql seed data (the IN_REVIEW thought with no evaluations)
        given()
            .redirects().follow(false)
            .when().post("/thoughts/d4e5f6a7-b8c9-0123-defa-234567890123/delete")
            .then()
                .statusCode(303)
                .header("Location", containsString("/thoughts"));
    }
}
