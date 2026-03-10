package com.redhat.demos.thoughts.admin;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ThoughtResourceTest {

    @Test
    public void testThoughtsListReturns200() {
        given()
            .when().get("/thoughts")
            .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("Thoughts"));
    }

    @Test
    public void testThoughtsListRespectsPagination() {
        given()
            .queryParam("page", 1)
            .queryParam("size", 5)
            .when().get("/thoughts")
            .then()
                .statusCode(200)
                .body(containsString("Page 2"));
    }

    @Test
    public void testThoughtDetailReturns200() {
        // Use a UUID from import.sql seed data
        given()
            .when().get("/thoughts/a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            .then()
                .statusCode(200)
                .body(containsString("The only way to do great work is to love what you do"))
                .body(containsString("Steve Jobs"));
    }

    @Test
    public void testThoughtDetailNotFound() {
        given()
            .when().get("/thoughts/00000000-0000-0000-0000-000000000000")
            .then()
                .statusCode(404);
    }
}
