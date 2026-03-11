package com.redhat.demos.thoughts.admin;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class AdminSiteStylingStatCardsTest {

    @Test
    public void testStatCardsHaveBoxShadow() {
        given()
            .when().get("/css/styles.css")
            .then()
                .statusCode(200)
                .body(containsString("box-shadow"));
    }

    @Test
    public void testStatCardsHaveGradientBackgrounds() {
        given()
            .when().get("/css/styles.css")
            .then()
                .statusCode(200)
                .body(containsString("linear-gradient"));
    }

    @Test
    public void testDashboardStatCardsHaveBootstrapIcons() {
        given()
            .when().get("/")
            .then()
                .statusCode(200)
                .body(containsString("bi bi-lightbulb"))
                .body(containsString("bi bi-hand-thumbs-up"))
                .body(containsString("bi bi-hand-thumbs-down"));
    }
}
