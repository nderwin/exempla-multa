package com.github.nderwin.scrolling.boundary;

import com.github.nderwin.scrolling.control.ProductRepository;
import com.github.nderwin.scrolling.entity.Product;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Produces(APPLICATION_JSON)
@Path("products")
@RequestScoped
public class ProductResource {
    
    @Inject
    ProductRepository repo;
    
    @GET
    public PageResponse<ProductDTO> list(
            @QueryParam("category")
            final String category,

            @QueryParam("cursor")
            final String cursor,

            @DefaultValue("20")
            @QueryParam("limit")
            final int limit
    ) {
        final int theLimit = Math.min(limit, 100);
        
        List<Product> results = repo.findByPopularity(category, cursor, theLimit + 1);

        final boolean hasMore = results.size() > theLimit;
        if (hasMore) {
            results = results.subList(0, theLimit);
        }
        
        final List<ProductDTO> data = results.stream()
                .map(this::toDTO)
                .toList();
        
        final String nextCursor = hasMore && !results.isEmpty()
                ? encodeCursor(results.get(results.size() - 1))
                : null;
        
        return new PageResponse<>(
                data,
                nextCursor,
                hasMore,
                data.size()
        );
    }
    
    @Path("seed")
    @POST
    @Transactional
    public String seed(
            @DefaultValue("10000")
            @QueryParam("count")
            final int count
    ) {
        repo.deleteAll();
        
        for (int i = 0; i < count; i++) {
            new Product(
                    "Product " + i,
                    i % 2 == 0 ? "Electronics" : "Books",
                    new BigDecimal(10.0d + (i % 100)),
                    (int) (Math.random() * 10000),
                    Instant.now().minusSeconds(i * 60)
            ).persist();
        }
        
        return "Seeded " + count + " product(s)";
    }
    
    private ProductDTO toDTO(final Product p) {
        return new ProductDTO(
                p.getId(), 
                p.getName(), 
                p.getCategory(), 
                p.getPrice(), 
                p.getViewCount(), 
                p.getCreatedAt().toString(), 
                encodeCursor(p));
    }
    
    private String encodeCursor(final Product p) {
        return p.getViewCount() + ":" + p.getId();
    }
    
}
