package com.github.nderwin.event.sourcing.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nderwin.event.sourcing.control.OrderEvent.ItemAdded;
import com.github.nderwin.event.sourcing.control.OrderEvent.ItemRemoved;
import com.github.nderwin.event.sourcing.control.OrderEvent.OrderCancelled;
import com.github.nderwin.event.sourcing.control.OrderEvent.OrderPlaced;
import com.github.nderwin.event.sourcing.control.OrderEvent.OrderShipped;
import com.github.nderwin.event.sourcing.entity.StoredEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
public class EventStore {
    
    @Inject
    ObjectMapper mapper;
    
    @Inject
    Event<OrderEvent> eventBus;
    
    @Transactional
    public void append(final UUID aggregateId, final String aggregateType, final OrderEvent event) {
        final StoredEvent stored = new StoredEvent(
                aggregateId,
                aggregateType,
                nextVersion(aggregateId),
                eventType(event),
                serialize(event),
                event.timestamp()
        );
        
        stored.persist();
        
        eventBus.fire(event);
    }
    
    @Transactional(SUPPORTS)
    public List<OrderEvent> loadEvents(final UUID aggregateId) {
        // TODO method in StoreEvent
        final List<StoredEvent> rows = StoredEvent.list("aggregateId = ?1 ORDER BY version", aggregateId);
        
        return rows.stream().map(this::deserialize).toList();
    }
    
    private long nextVersion(final UUID aggregateId) {
        long count = StoredEvent.count("aggregateId", aggregateId);
        return count + 1;
    }
    
    private String eventType(final OrderEvent event) {
        return event.getClass().getSimpleName();
    }

    private String serialize(final OrderEvent event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (final JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize event " + event, ex);
        }
    }

    private OrderEvent deserialize(final StoredEvent stored) {
        try {
            return switch (stored.getEventType()) {
                case "OrderPlaced" -> mapper.readValue(stored.getEventData(), OrderPlaced.class);
                case "ItemAdded" -> mapper.readValue(stored.getEventData(), ItemAdded.class);
                case "ItemRemoved" -> mapper.readValue(stored.getEventData(), ItemRemoved.class);
                case "OrderCancelled" -> mapper.readValue(stored.getEventData(), OrderCancelled.class);
                case "OrderShipped" -> mapper.readValue(stored.getEventData(), OrderShipped.class);
                default -> throw new IllegalArgumentException("Unknown event type " + stored.getEventType());
            };
        } catch (final IOException ex) {
            throw new IllegalStateException("Could not deserialize event " + stored.id, ex);
        }
    }
    
}
