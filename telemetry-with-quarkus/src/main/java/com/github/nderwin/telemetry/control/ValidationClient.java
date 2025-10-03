/*
 * Copyright 2025 Nathan Erwin
 */
package com.github.nderwin.telemetry.control;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Produces(MediaType.APPLICATION_JSON)
@Path("validations")
@RegisterRestClient(baseUri = "http://localhost:8080/telemetry", configKey = "validation-client")
public interface ValidationClient {

//    @WithSpan(kind = SpanKind.CLIENT)
    @POST
    Response validate(
            final VerificationRequest body
    );

}
