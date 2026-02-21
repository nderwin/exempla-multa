package com.github.nderwin.api.adapter.boundary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nderwin.api.adapter.control.AdapterRegistry;
import com.github.nderwin.api.adapter.control.PaymentService;
import com.github.nderwin.api.adapter.control.RequestAdapter;
import com.github.nderwin.api.adapter.control.ResponseAdapter;
import com.github.nderwin.api.adapter.entity.CanonicalPayment;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.extensions.Extension;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.HEADER;

@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Path("payments")
@RequestScoped
public class PaymentResource {
    
    @Inject
    VersionContext versionContext;
    
    @Inject
    AdapterRegistry registry;
    
    @Inject
    PaymentService service;
    
    @Inject
    ObjectMapper mapper;
    
    /*
        OpenAPI documentation will be messy...  unless you use the media type
        as the version discriminator, which is a better way than some custom
        reuqest header.  Need to figure out how to combine the date bit with
        the standard format:
    
        application/vnd.company.product[.resource][.version][+suffix]
     */
    @Operation(extensions = {
        @Extension(name = "X-API-Version", value = "2024-09-01")
    })
    @Parameter(in = HEADER, name = "X-API-Version", required = false, description = "ISO Date corresponding to a given API version", example = "2024-09-01")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(oneOf = {PaymentV1.class, PaymentV2.class, PaymentV3Response.class})))
    })
    @POST
    public Response createPayment(
            @RequestBody(
                    description = "Versioned payment request",
                    content = {
                        @Content(
                                mediaType = APPLICATION_JSON, 
                                schema = @Schema(
                                        oneOf = {PaymentV1.class, PaymentV2.class}
                                )
                        )
                    }
            )
            @Valid
            final JsonNode body
    ) throws Exception {
        final String version = versionContext.getVersion();
        
        final RequestAdapter<?> reqAdapter = pickRequestAdapter(version);
        final Object typedReq = mapper.treeToValue(body, reqAdapter.requestType());
        final CanonicalPayment canonical = adaptToCanonical(reqAdapter, typedReq);
        
        final CanonicalPayment created = service.create(canonical);
        
        final Object versionedResponse = pickAndTransformResponse(version, created);
        
        return Response.ok(versionedResponse).build();
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    private CanonicalPayment adaptToCanonical(final RequestAdapter adapter, final Object typedReq) {
        return adapter.toCanonical(typedReq);
    }
    
    private RequestAdapter<?> pickRequestAdapter(final String version) {
        try {
            return registry.requestAdapterFor(version, PaymentV2.class);
        } catch (final Exception ignored) {
            return registry.requestAdapterFor(version, PaymentV1.class);
        }
    }
    
    private Object pickAndTransformResponse(final String version, final CanonicalPayment created) {
        try {
            final ResponseAdapter<PaymentV3Response> v3 = registry.responseAdapterFor(version, PaymentV3Response.class);
            
            return v3.fromCanonical(created);
        } catch (final Exception ignore) {
        }
        
        try {
            final ResponseAdapter<PaymentV2> v2 = registry.responseAdapterFor(version, PaymentV2.class);
            
            return v2.fromCanonical(created);
        } catch (final Exception ignore) {
        }
        
        final ResponseAdapter<PaymentV1> v1 = registry.responseAdapterFor(version, PaymentV1.class);
        return v1.fromCanonical(created);
    }
}
