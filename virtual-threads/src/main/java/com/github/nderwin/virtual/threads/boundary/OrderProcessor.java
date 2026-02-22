package com.github.nderwin.virtual.threads.boundary;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class OrderProcessor {
    
//    @Incoming("orders")
//    @RunOnVirtualThread
    public void process(final String orderJson) {
        System.out.println("Order processed by: " + Thread.currentThread());
    }
}
