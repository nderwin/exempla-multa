package com.github.nderwin.virtual.threads.control;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class DatabaseSimulator {
    
    public String queryDatabase(final String query) {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        
        return "Result for: " + query;
    }
    
    public String slowQuery(final long delayMs) {
        try {
            TimeUnit.MILLISECONDS.sleep(delayMs);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        
        return "Slow query took " + delayMs + "ms";
    }
    
}
