package com.github.nderwin.claims.control;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class ClaimValidatedDeserializer extends ObjectMapperDeserializer<ClaimValidated> {

    public ClaimValidatedDeserializer() {
        super(ClaimValidated.class);
    }

}
