package com.github.nderwin.api.adapter.boundary;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.aMapWithSize;

@QuarkusTest
public class PaymentResourceTest {
    
    @Test
    public void testV1RequestAndResponse() {
        given()
                .header("X-API-Version", "2023-10-16")
                .contentType(JSON)
                .body("{\"amount\": 12.34}")
                .when()
                .post("payments")
                .then()
                .statusCode(OK.getStatusCode())
                .body("amount", equalTo(12.34f))
                .body("$", aMapWithSize(1));
    }

    @Test
    public void testV2RequestAndResponse() {
        given()
                .header("X-API-Version", "2024-03-15")
                .contentType(JSON)
                .body("{\"amount\": 99.99, \"method\": \"CARD\"}")
                .when()
                .post("payments")
                .then()
                .statusCode(OK.getStatusCode())
                .body("amount", equalTo(99.99f))
                .body("method", equalTo("CARD"));
    }
    
    @Test
    public void testV3RequestAndResponse() {
        given()
                .header("X-API-Version", "2024-09-01")
                .contentType(JSON)
                .body("{\"amount\": 50.00, \"method\": \"SEPA\"}")
                .when()
                .post("payments")
                .then()
                .statusCode(OK.getStatusCode())
                .body("id", notNullValue())
                .body("amount", equalTo(50.00f))
                .body("method", equalTo("SEPA"))
                .body("status", anyOf(equalTo("AUTHORIZED"), equalTo("PENDING")))
                .body("confirmationRequired", isA(Boolean.class));
    }
    
    @Test
    public void testDefaultVersion() {
        given()
                .contentType(JSON)
                .body("{\"amount\": 10.00, \"method\": \"CARD\"}")
                .when()
                .post("payments")
                .then()
                .statusCode(OK.getStatusCode())
                .body("id", notNullValue())
                .body("amount", equalTo(10.00f))
                .body("method", equalTo("CARD"))
                .body("status", anyOf(equalTo("AUTHORIZED"), equalTo("PENDING")))
                .body("confirmationRequired", isA(Boolean.class));
    }
    
}
