package com.github.nderwin.feature.flags.boundary;

import com.github.nderwin.feature.flags.control.ProductService;
import com.github.nderwin.feature.flags.entity.FeatureFlag;
import com.github.nderwin.feature.flags.entity.Product;
import io.quarkiverse.flags.Flags;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("products")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RequestScoped
public class ProductResource {

    @Inject
    ProductService service;
    
    @Inject
    Flags flags;
    
    @GET
    public List<Product> getProducts() {
        return service.getProductsWithDetails();
    }
    
    @Path("bulk-update")
    @GET
    public Response bulkUpdatePrices(
            @Positive
            @QueryParam("multiplier")
            final BigDecimal multiplier
    ) {
        try {
            final List<Product> updated = service.bulkUpdatePrices(multiplier);
            return Response.ok(updated).build();
        } catch (final IllegalStateException ex) {
            throw new ForbiddenException(ex);
        }
    }
    
    @Path("features")
    @GET
    public Map<String, Boolean> getFeatureStatus() {
        return Map.of(
                "premium-features", safeIsEnabled("premium-features"),
                "bulk-operations", safeIsEnabled("bulk-operations"),
                "analytics-dashboard", safeIsEnabled("analytics-dashboard"),
                "new-ui", safeIsEnabled("new-ui"),
                "spring-sale", safeIsEnabled("spring-sale")
        );
    }
    
    @Transactional
    @Path("features/bulk-operations/toggle")
    @PUT
    public Response toggleBulkOperations() {
        FeatureFlag flag = FeatureFlag.find("feature", "bulk-operations").firstResult();
        
        if (null == flag) {
            throw new NotFoundException("bulk-operations flag not found");
        }
        
        // Toggle the value
        final boolean currentValue = Boolean.parseBoolean(flag.value);
        flag.value = currentValue ? "false" : "true";
        flag.persist();
        
        return Response.ok(Map.of(
                "flag", "bulk-operations",
                "enabled", !currentValue,
                "message", "Flag toggled successfully"))
                .build();
        
    }
    
    private boolean safeIsEnabled(final String flagName) {
        try {
            return flags.isEnabled(flagName);
        } catch (Exception e) {
            return false;
        }
    }

}
