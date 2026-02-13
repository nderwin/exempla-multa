package com.github.nderwin.event.sourcing.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "eventstore", indexes = {
    @Index(name = "idx_event_aggregate", columnList = "aggregateId, version")
})
public class StoredEvent extends PanacheEntity {
    
    @Column(nullable = false, columnDefinition = "uuid")
    private UUID aggregateId;
    
    @Column(nullable = false, length = 64)
    private String aggregateType;
    
    @Column(nullable = false)
    private long version;
    
    @Column(nullable = false, length = 64)
    private String eventType;

    @Column(nullable = false, columnDefinition = "text")
    private String eventData;
    
    @Column(nullable = false)
    private Instant timestamp;

    protected StoredEvent() {
    }

    public StoredEvent(
            final UUID aggregateId, 
            final String aggregateType, 
            final long version, 
            final String eventType, 
            final String eventData, 
            final Instant timestamp) {
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.version = version;
        this.eventType = eventType;
        this.eventData = eventData;
        this.timestamp = timestamp;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public long getVersion() {
        return version;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventData() {
        return eventData;
    }

}
