package com.github.nderwin.api.adapter.boundary;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
@ApplicationScoped
public class ApiVersionFilter implements ContainerRequestFilter {
    
    @ConfigProperty(name = "api.default-version", defaultValue = "2024-09-01")
    String defaultVersion;
    
    @Inject
    VersionContext context;

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        final String v = requestContext.getHeaderString("X-API-Version");
        context.setVersion(null != v && !v.isBlank() ? v : defaultVersion);
    }
    
}
