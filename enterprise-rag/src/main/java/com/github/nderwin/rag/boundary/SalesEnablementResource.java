package com.github.nderwin.rag.boundary;

import com.github.nderwin.rag.control.BotResponse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("bot")
@RequestScoped
public class SalesEnablementResource {
    
    @Inject
    SalesEnablementBot bot;
    
    @GET
    @Produces(APPLICATION_JSON)
    public BotResponse ask(
            @QueryParam("q")
            final String q
    ) {
        String question = q;
        if (null == q || q.isBlank()) {
            question = "What is the best solution for a client who is migrating to a microservices architecture?";
        }
        
        final String botResponse = bot.chat(question);
        return new BotResponse(botResponse);
    }

}
