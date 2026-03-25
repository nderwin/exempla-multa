package com.github.nderwin.rag.control;

import dev.langchain4j.guardrail.InputGuardrailException;
import io.quarkus.logging.Log;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public class InputGuardrailExceptionMapper implements ExceptionMapper<InputGuardrailException> {

    @Override
    public Response toResponse(final InputGuardrailException exception) {
        Log.warn("InputGuardrailException caught: " + exception.getMessage());
        
        String message = exception.getMessage();
        if (null == message || message.isBlank()) {
            message = "Input validation failed.  Please ensure your question is related to CloudX Enterprise Platform sales enablement.";
        }
        
        final BotResponse errorResponse = new BotResponse(message);
        
        return Response.status(BAD_REQUEST)
                .entity(errorResponse)
                .type(APPLICATION_JSON)
                .build();
    }
    
}
