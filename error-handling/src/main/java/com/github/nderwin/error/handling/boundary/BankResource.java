package com.github.nderwin.error.handling.boundary;

import io.quarkiverse.resteasy.problem.HttpProblem;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import static com.github.nderwin.error.handling.control.ErrorRegistry.INSUFFICIENT_FUNDS;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

@Produces(APPLICATION_JSON)
@Path("bank")
@RequestScoped
public class BankResource {
    
    @Context
    UriInfo uriInfo;

    @Path("withdraw/{amount}")
    @GET
    public String withdraw(
            @PathParam("amount")
            final int amount
    ) {
        if (amount > 1000) {
            throw HttpProblem.builder()
                    .withType(uriInfo.getBaseUri().resolve(INSUFFICIENT_FUNDS.getType()))
                    .withTitle(INSUFFICIENT_FUNDS.getTitle())
                    .withStatus(BAD_REQUEST)
                    .withDetail("Current balance is 500, but you requested " + amount)
                    .with("current_balance", 500)
                    .with("requested_amount", amount)
                    .build();
        }
        
        if (amount < 0) {
            throw new IllegalArgumentException("Withdrawal amount cannot be negative");
        }
        
        return "{\"message\": \"Withdrawal successful\"}";
    }
}
