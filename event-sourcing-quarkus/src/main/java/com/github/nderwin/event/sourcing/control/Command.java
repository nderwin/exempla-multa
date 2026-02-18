package com.github.nderwin.event.sourcing.control;

import java.math.BigDecimal;
import java.util.UUID;

public final class Command {

    private Command() {
    }
    
    public record PlaceOrder(String customerEmail) {
        
    }
    
    public record AddItem(UUID orderId, String productName, int quantity, BigDecimal price) {
        
    }
    
    public record ShipOrder(UUID orderId, String trackingNumber) {
        
    }
    
    public record CancelOrder(UUID orderId, String reason) {
        
    }
    
}
