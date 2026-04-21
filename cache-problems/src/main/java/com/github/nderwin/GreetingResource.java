package com.github.nderwin;

import io.quarkiverse.resteasy.problem.HttpProblem;
import io.quarkus.cache.CacheResult;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.time.Duration;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

@Path("hello")
public class GreetingResource {

    @CacheResult(cacheName = "my-fast-cache")
    @GET
    @Path("fast")
    @Produces(APPLICATION_JSON)
    public Response fastHello() {
        return Response.ok(new Greeting("Hello from Quarkus REST")).build();
    }

    @CacheResult(cacheName = "my-slow-cache")
    @GET
    @Path("slow")
    @Produces(APPLICATION_JSON)
    public Response slowHello() throws InterruptedException {
        Thread.sleep(Duration.ofMillis(500));

        return Response.ok(new Greeting("Hello...  from...  Quarkus...  REST...")).build();
    }

    @CacheResult(cacheName = "my-fail-cache")
    @GET
    @Path("fail")
    @Produces(APPLICATION_JSON)
    public Response slowFail() throws InterruptedException {
        Thread.sleep(Duration.ofMillis(500));

        return HttpProblem.builder().withStatus(NOT_FOUND).build().toResponse();
    }

    public record Greeting(String message) {

    }

}
