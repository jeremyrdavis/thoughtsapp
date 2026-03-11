package com.redhat.demos.thoughts.admin;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class AdminSiteStylingCssTest {

    @Test
    public void testNavbarOverriddenToRedHatRed() {
        given()
            .when().get("/css/styles.css")
            .then()
                .statusCode(200)
                .body(containsString(".navbar.bg-dark"))
                .body(containsString("#EE0000"));
    }

    @Test
    public void testHeadingsUseRedHatDisplayFont() {
        given()
            .when().get("/css/styles.css")
            .then()
                .statusCode(200)
                .body(containsString("h1, h2, h3, h4, h5, h6"))
                .body(containsString("Red Hat Display"));
    }

    @Test
    public void testBodyUsesRedHatTextFont() {
        given()
            .when().get("/css/styles.css")
            .then()
                .statusCode(200)
                .body(containsString("Red Hat Text"));
    }

    @Test
    public void testFooterHasDarkBackgroundAndWhiteText() {
        given()
            .when().get("/css/styles.css")
            .then()
                .statusCode(200)
                .body(containsString("--rh-black"))
                .body(containsString("--rh-white"));
    }
}
