package com.github.nderwin.event.sourcing.control;

import com.github.nderwin.event.sourcing.entity.OrderReadModel;
import com.github.nderwin.event.sourcing.entity.OrderState;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OrderProjector {
    
    @Inject
    EventStore store;
    
    @Transactional
    void on(@Observes final OrderEvent event) {
        final UUID orderId = event.orderId();
        
        final List<OrderEvent> events = store.loadEvents(orderId);
        final OrderState state = EventProjection.replayEvents(events);
        
        OrderReadModel model = OrderReadModel.findByOrderId(orderId);
        if (null == model) {
            model = new OrderReadModel(orderId);
        }
        
        model.setCustomerEmail(state.customerEmail());
        model.setStatus(state.status());
        model.setTotal(state.total());
        
        if (!model.isPersistent()) {
            model.persist();
        }
    }
}
