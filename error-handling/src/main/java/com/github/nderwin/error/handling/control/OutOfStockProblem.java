package com.github.nderwin.error.handling.control;

import io.quarkiverse.resteasy.problem.HttpProblem;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class OutOfStockProblem extends HttpProblem {

    public OutOfStockProblem(final String message) {
        super(builder()
                .withTitle("Bad hello request")
                .withStatus(BAD_REQUEST)
                .withDetail(message)
                .withHeader("X-RFC7807-Message", message)
                .with("hello", "world"));
    }

}
