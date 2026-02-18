package com.github.nderwin.feature.flags.control;

import com.github.nderwin.feature.flags.entity.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkiverse.flags.Flags;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService {
    
    @Inject
    Flags flags;
    
    private final List<Product> products = Arrays.asList(
            new Product(1L, "Laptop", "Electronics", new BigDecimal("999.99"), "LAP-001"),
            new Product(2L, "Mouse", "Electronics", new BigDecimal("29.99"), "MOU-001"),
            new Product(3L, "Desk", "Furniture", new BigDecimal("299.99"), "DSK-001"),
            new Product(4L, "Chair", "Furniture", new BigDecimal("199.99"), "CHR-001")
    );

    public List<Product> getAllProducts() {
        return products;
    }
    
    public List<Product> getProductsWithDetails() {
        if (flags.isEnabled("premium-features")) {
            return products;
        }
        
        return products.stream()
                .map(p -> new Product(p.getId(), p.getName(), p.getCategory(), p.getPrice(), null))
                .collect(Collectors.toList());
    }
    
    public boolean canPerformBulkOperations() {
        return flags.isEnabled("bulk-operations");
    }
    
    public List<Product> bulkUpdatePrices(final BigDecimal multiplier) {
        if (!canPerformBulkOperations()) {
            throw new IllegalStateException("Bulk operations feature is disabled");
        }
        
        return products.stream()
                .peek(p -> p.setPrice(p.getPrice().multiply(multiplier)))
                .collect(Collectors.toList());
    }
    
}
