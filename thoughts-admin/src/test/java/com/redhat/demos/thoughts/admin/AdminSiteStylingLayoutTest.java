package com.redhat.demos.thoughts.admin;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class AdminSiteStylingLayoutTest {

    @Test
    public void testLayoutIncludesGoogleFontsLink() {
        given()
            .when().get("/")
            .then()
                .statusCode(200)
                .body(containsString("fonts.googleapis.com"))
                .body(containsString("Red+Hat+Display"))
                .body(containsString("Red+Hat+Text"));
    }

    @Test
    public void testLayoutIncludesBootstrapIconsCdn() {
        given()
            .when().get("/")
            .then()
                .statusCode(200)
                .body(containsString("bootstrap-icons"));
    }

    @Test
    public void testLayoutIncludesCustomStylesheet() {
        given()
            .when().get("/")
            .then()
                .statusCode(200)
                .body(containsString("/css/styles.css"));
    }
}
