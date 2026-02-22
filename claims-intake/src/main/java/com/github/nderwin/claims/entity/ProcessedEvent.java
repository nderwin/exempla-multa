package com.github.nderwin.claims.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "processed_event")
@Entity
public class ProcessedEvent extends PanacheEntityBase {
    
    @Id
    private String eventId;

    protected ProcessedEvent() {
    }
    
    public ProcessedEvent(final String eventId) {
        this.eventId = eventId;
    }
    
    public static boolean alreadyProcessed(final String eventId) {
        return findById(eventId) != null;
    }
    
    public static void markProcessed(final String eventId) {
        final ProcessedEvent e = new ProcessedEvent(eventId);
        e.persist();
    }

    public String getEventId() {
        return eventId;
    }
    
}
