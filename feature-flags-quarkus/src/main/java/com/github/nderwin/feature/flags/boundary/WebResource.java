package com.github.nderwin.feature.flags.boundary;

import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/")
public class WebResource {
    
    @Inject
    Template index;

    @GET
    public Object page() {
        return index.instance();
    }
    
}
