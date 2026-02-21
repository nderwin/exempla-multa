package com.github.nderwin.error.handling.control;

import java.util.Collections;
import java.util.HashMap;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;

import static org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.INTEGER;
import static org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.OBJECT;
import static org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.STRING;

public class GlobalErrorResponseFilter implements OASFilter {

    @Override
    public void filterOpenAPI(final OpenAPI openAPI) {
        ensureProblemSchema(openAPI);
        
        openAPI.getPaths().getPathItems().values()
                .forEach(pathItem -> pathItem.getOperations().values().forEach(operation -> {
                    if (!operation.getResponses().hasAPIResponse("400")) {
                        operation.getResponses().addAPIResponse("400", createProblemResponse("Bad Request"));
                    }
                    
                    if (!operation.getResponses().hasAPIResponse("500")) {
                        operation.getResponses().addAPIResponse("500", createProblemResponse("Internal Server Error"));
                    }
                }));
    }
    
    private void ensureProblemSchema(final OpenAPI openAPI) {
        if (null == openAPI.getComponents()) {
            openAPI.setComponents(OASFactory.createComponents());
        }
        
        var existingSchemas = openAPI.getComponents().getSchemas();
        
        if (null == existingSchemas || !existingSchemas.containsKey("Problem")) {
            final Schema typeSchema = OASFactory.createSchema()
                    .type(Collections.singletonList(STRING))
                    .format("uri")
                    .description("A URI reference that identifies the problem type");
            
            final Schema titleSchema = OASFactory.createSchema()
                    .type(Collections.singletonList(STRING))
                    .description("A short, human-readable summary of the problem type.");
            
            final Schema statusSchema = OASFactory.createSchema()
                    .type(Collections.singletonList(INTEGER))
                    .format("int32")
                    .description("The HTTP status code");
            
            final Schema detailSchema = OASFactory.createSchema()
                    .type(Collections.singletonList(STRING))
                    .description("A human-readable explanation specific to this occurrence of the problem");
            
            final Schema instanceSchema = OASFactory.createSchema()
                    .type(Collections.singletonList(STRING))
                    .format("uri")
                    .description("A URI reference that identifies the specific occurrence of the problem");
            
            final Schema problemSchema = OASFactory.createSchema()
                    .type(Collections.singletonList(OBJECT))
                    .addProperty("type", typeSchema)
                    .addProperty("title", titleSchema)
                    .addProperty("status", statusSchema)
                    .addProperty("detail", detailSchema)
                    .addProperty("instance", instanceSchema)
                    .description("RFC 7807 Problem Details for HTTP APIs (compatible with RFC 9457)");
                    
            var schemas = new HashMap<String, Schema>();
            if (null != existingSchemas) {
                schemas.putAll(existingSchemas);
            }
            schemas.put("Problem", problemSchema);
            openAPI.getComponents().setSchemas(schemas);
        }
    }
    
    private APIResponse createProblemResponse(final String description) {
        final Schema problemSchema = OASFactory.createSchema()
                .ref("#/components/schemas/Problem");
        
        return OASFactory.createAPIResponse()
                .description(description)
                .content(OASFactory.createContent()
                        .addMediaType("application/problem+json", OASFactory.createMediaType().schema(problemSchema))
                );
    }
    
}
