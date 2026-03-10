package com.redhat.demos.thoughts.admin;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class IntegrationWorkflowTest {

    @Test
    public void testCreateThenVerifyInList() {
        // Create a thought
        String location = given()
            .formParam("content", "Integration test thought for workflow verification testing")
            .formParam("author", "Workflow Author")
            .formParam("authorBio", "Workflow bio")
            .redirects().follow(false)
            .when().post("/thoughts/create")
            .then()
                .statusCode(303)
                .extract().header("Location");

        // Verify it appears in the list
        given()
            .when().get("/thoughts")
            .then()
                .statusCode(200)
                .body(containsString("Integration test thought for workflow"));
    }

    @Test
    public void testCreateThenEditThenVerify() {
        // Create a thought
        String location = given()
            .formParam("content", "Original content for edit workflow test verification")
            .formParam("author", "Edit Workflow Author")
            .formParam("authorBio", "Edit bio")
            .redirects().follow(false)
            .when().post("/thoughts/create")
            .then()
                .statusCode(303)
                .extract().header("Location");

        // Extract the ID from the location
        String id = location.substring(location.lastIndexOf("/") + 1);

        // Edit the thought
        given()
            .formParam("content", "Updated content for edit workflow test verification now")
            .formParam("author", "Updated Author")
            .formParam("authorBio", "Updated bio")
            .redirects().follow(false)
            .when().post("/thoughts/" + id + "/edit")
            .then()
                .statusCode(303);

        // Verify the edit took effect
        given()
            .when().get("/thoughts/" + id)
            .then()
                .statusCode(200)
                .body(containsString("Updated content for edit workflow test verification now"))
                .body(containsString("Updated Author"));
    }

    @Test
    public void testCreateThenDeleteThenVerifyGone() {
        // Create a thought
        String location = given()
            .formParam("content", "A thought that will be deleted in the workflow test")
            .formParam("author", "Delete Workflow Author")
            .formParam("authorBio", "Delete bio")
            .redirects().follow(false)
            .when().post("/thoughts/create")
            .then()
                .statusCode(303)
                .extract().header("Location");

        String id = location.substring(location.lastIndexOf("/") + 1);

        // Delete it
        given()
            .redirects().follow(false)
            .when().post("/thoughts/" + id + "/delete")
            .then()
                .statusCode(303);

        // Verify it's gone
        given()
            .when().get("/thoughts/" + id)
            .then()
                .statusCode(404);
    }

    @Test
    public void testNavigationLinksInLayout() {
        given()
            .when().get("/")
            .then()
                .statusCode(200)
                .body(containsString("href=\"/\""))
                .body(containsString("href=\"/thoughts\""))
                .body(containsString("href=\"/ratings\""))
                .body(containsString("href=\"/evaluations\""));
    }

    @Test
    public void testDashboardStatsReflectData() {
        given()
            .when().get("/")
            .then()
                .statusCode(200)
                .body(containsString("Approved"))
                .body(containsString("Rejected"))
                .body(containsString("In Review"));
    }

    @Test
    public void testEmptyPaginationPage() {
        given()
            .queryParam("page", 999)
            .queryParam("size", 20)
            .when().get("/thoughts")
            .then()
                .statusCode(200);
    }

    @Test
    public void testEvaluationDetailViaThoughtPage() {
        // The seed data has evaluations linked to thought a1b2c3d4-...
        given()
            .when().get("/thoughts/a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            .then()
                .statusCode(200)
                .body(containsString("AI Evaluations"))
                .body(containsString("Similarity Score"));
    }

    @Test
    public void testRatingsSortOptions() {
        given()
            .queryParam("sort", "most-disliked")
            .when().get("/ratings")
            .then()
                .statusCode(200)
                .body(containsString("Most Disliked"));
    }

    @Test
    public void testEvaluationsStatsShowsAggregates() {
        given()
            .when().get("/evaluations/stats")
            .then()
                .statusCode(200)
                .body(containsString("Total Evaluations"))
                .body(containsString("Approved"))
                .body(containsString("Rejected"))
                .body(containsString("Avg Similarity Score"));
    }

    @Test
    public void testCreateFormValidationPreservesInput() {
        given()
            .formParam("content", "short")
            .formParam("author", "Preserved Author")
            .formParam("authorBio", "Preserved bio")
            .when().post("/thoughts/create")
            .then()
                .statusCode(200)
                .body(containsString("short"))
                .body(containsString("Preserved Author"));
    }
}
