package com.redhat.demos.thoughts.admin;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ObservabilityTest {

    @Test
    public void testLivenessProbe() {
        given()
            .when().get("/q/health/live")
            .then()
                .statusCode(200)
                .body(containsString("UP"));
    }

    @Test
    public void testReadinessProbe() {
        given()
            .when().get("/q/health/ready")
            .then()
                .statusCode(200)
                .body(containsString("UP"));
    }

    @Test
    public void testMetricsEndpoint() {
        given()
            .when().get("/q/metrics")
            .then()
                .statusCode(200);
    }
}
