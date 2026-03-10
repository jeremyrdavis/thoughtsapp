package com.redhat.demos.thoughts.admin;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class DashboardResourceTest {

    @Test
    public void testDashboardReturns200WithHtml() {
        given()
            .when().get("/")
            .then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    @Test
    public void testDashboardContainsSummaryStatistics() {
        given()
            .when().get("/")
            .then()
                .statusCode(200)
                .body(containsString("Total Thoughts"))
                .body(containsString("Total Thumbs Up"))
                .body(containsString("Total Thumbs Down"));
    }

    @Test
    public void testDashboardShowsRecentActivity() {
        given()
            .when().get("/")
            .then()
                .statusCode(200)
                .body(containsString("Recent Activity"));
    }
}
