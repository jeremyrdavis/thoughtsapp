package com.redhat.demos.thoughts.admin;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class RatingsAndEvaluationsTest {

    @Test
    public void testRatingsPageReturns200() {
        given()
            .when().get("/ratings")
            .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("Ratings Overview"));
    }

    @Test
    public void testRatingsSortByMostLiked() {
        given()
            .queryParam("sort", "most-liked")
            .when().get("/ratings")
            .then()
                .statusCode(200)
                .body(containsString("Most Liked"));
    }

    @Test
    public void testEvaluationsPageReturns200() {
        given()
            .when().get("/evaluations")
            .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("AI Evaluations"));
    }

    @Test
    public void testEvaluationsFilterByStatus() {
        given()
            .queryParam("status", "APPROVED")
            .when().get("/evaluations")
            .then()
                .statusCode(200);
    }

    @Test
    public void testEvaluationsStatsReturns200() {
        given()
            .when().get("/evaluations/stats")
            .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("Evaluation Statistics"));
    }
}
