package com.github.nderwin.virtual.threads.boundary;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class VirtualThreadResourceTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/api/demo")
          .then()
             .statusCode(200)
             .body(is("Hello from Quarkus REST"));
    }

}