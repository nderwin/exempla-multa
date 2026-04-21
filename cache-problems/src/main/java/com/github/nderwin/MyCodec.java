package com.github.nderwin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.quarkus.redis.datasource.codecs.Codec;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Type;

@ApplicationScoped
public class MyCodec implements Codec {

    @Inject
    ObjectMapper mapper;

    @Override
    public boolean canHandle(Type clazz) {
        return clazz.equals(Response.class);
    }

    @Override
    public byte[] encode(Object item) {
        // The Codecs.JsonCodec will handle this for us
        throw new UnsupportedOperationException("Should not be called");
    }
    
    @Override
    public Object decode(byte[] item) {
        try {
            final JsonNode json = mapper.readTree(item);
            
            return Response
                    .status(json.get("status").asInt())
                    .entity(json.get("entity"))
                    .build();
        } catch (IOException ex) {
            Log.error(ex.getMessage());
            return Response.serverError().build();
        }
    }

}
