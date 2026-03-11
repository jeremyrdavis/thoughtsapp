package com.redhat.demos.thoughts.admin;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class AdminSiteStylingVerificationTest {

    @Test
    public void testFooterRendersOnThoughtsListPage() {
        given()
            .when().get("/thoughts")
            .then()
                .statusCode(200)
                .body(containsString("Positive Thoughts Admin"))
                .body(containsString("<footer>"));
    }

    @Test
    public void testFormPagesIncludeRedHatTextOnControls() {
        given()
            .when().get("/thoughts/create")
            .then()
                .statusCode(200)
                .body(containsString("form-control"))
                .body(containsString("form-label"))
                .body(containsString("/css/styles.css"));
    }

    @Test
    public void testTableLinksUseCustomStylesheet() {
        given()
            .when().get("/ratings")
            .then()
                .statusCode(200)
                .body(containsString("/css/styles.css"))
                .body(containsString("table"));
    }

    @Test
    public void testStatusBadgesRetainSemanticColors() {
        given()
            .when().get("/")
            .then()
                .statusCode(200)
                .body(containsString("bg-success"))
                .body(containsString("bg-danger"))
                .body(containsString("bg-warning"));
    }

    @Test
    public void testDisplayClassesRenderWithRedHatDisplayFont() {
        given()
            .when().get("/css/styles.css")
            .then()
                .statusCode(200)
                .body(containsString(".display-4"))
                .body(containsString(".display-6"))
                .body(containsString("Red Hat Display"));
    }
}
