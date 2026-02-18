package com.github.nderwin.data.oriented.control;

import com.github.nderwin.data.oriented.boundary.OrderRequest;
import com.github.nderwin.data.oriented.boundary.OrderResult;
import com.github.nderwin.data.oriented.boundary.OrderResult.OutOfStock;
import com.github.nderwin.data.oriented.boundary.OrderResult.ProductNotFound;
import com.github.nderwin.data.oriented.boundary.OrderResult.Success;
import com.github.nderwin.data.oriented.boundary.OrderView;
import com.github.nderwin.data.oriented.entity.Order;
import com.github.nderwin.data.oriented.entity.Product;
import java.math.BigDecimal;
import java.util.List;

/**
 * BIG TODO 
 *  - no transactions?  
 *  - leaking entity fields into this class?  
 *  - using things from the boundary package?
 */
public final class OrderOperations {
    
    private enum StockLevel {
        SUFFICIENT, INSUFFICIENT
    }
    
    private OrderOperations() {
        
    }
    
    public static List<OrderView> getAllOrders() {
        return Order.<Order>listAll()
                .stream()
                .map(OrderOperations::toView)
                .toList();
    }
    
    public static OrderResult placeOrder(final OrderRequest request) {
        final Product product = Product.find("name", request.productName()).firstResult();
        
        if (null == product) {
            return new ProductNotFound(request.productName());
        }
        
        return switch(compareStock(product.getStockQuantity(), request.quantity())) {
            case SUFFICIENT -> processOrder(request, product);
            case INSUFFICIENT -> new OutOfStock(
                    request.productName(),
                    product.getStockQuantity(),
                    request.quantity()
            );
        };
    }
    
    public static OrderView toView(final Order order) {
        return new OrderView(
                order.getId(),
                order.getCustomerEmail(),
                order.getProductName(),
                order.getQuantity(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt().toString()
        );
    }
    
    private static OrderResult processOrder(final OrderRequest request, final Product product) {
        final BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(request.quantity()));
        
        product.setStockQuantity(product.getStockQuantity() - request.quantity());
        product.persist();
        
        final Order order = new Order(
                request.customerEmail(),
                request.productName(),
                request.quantity(),
                total
        );
        
        order.persist();
        
        return new Success(
                order.getId(),
                order.getCustomerEmail(),
                order.getProductName(),
                order.getQuantity(),
                order.getTotalAmount()
        );
    }
    
    private static StockLevel compareStock(final int available, final int requested) {
        return available >= requested ? StockLevel.SUFFICIENT : StockLevel.INSUFFICIENT;
    }
    
}
