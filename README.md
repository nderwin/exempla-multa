# exempla-multa
Multiple Examples

## Overview

This repository contains several projects with examples of how to accomplish
various tasks or demonstrations of development patterns.

### Documentation

The [documentation](./documentation) project contains some descriptive text and 
UML diagrams.

### Telemetry

An example that spans multiple projects, this shows how to use OpenTelemetry
to track processes across disparate systems:

- [docker](./docker) - Docker setup for a database, RabbitMQ and the LGTM stack
- [telemetry-with-javaee](./telemetry-with-javaee) - a JavaEE 6 application,
deployed to JBoss EAP 6.4 that is instrumented with OpenTelemetry connected
as a Java agent
- [telemetry-with-quarkus](./telemetry-with-quarkus) - a Quarkus application
that uses the direct integration with OpenTelemetry in the code

### Others

These are all Quarkus applications demonstrating various extensions either with
Quarkus itself or the Quarkiverse.

- [api-adapter](./api-adapter) - using a custom header and date for versioning 
a REST API
- [claims-intake](./claims-intake) - reactive messaging with Kafka
- [credit-audit](./credit-audit) - using Hibernate Envers for auditing
- [data-oriented-quarkus](./data-oriented-quarkus) - data is data, operations
are functions, i.e. Data-Oriented Programming
- [error-handling](./error-handling) - using the Problem extension for 
standardized REST API error responses
- [event-sourcing-quarkus](./event-sourcing-quarkus) - CQRS, immutable events
are persisted
- [feature-flags-quarkus](./feature-flags-quarkus) - using feature flags to 
control application behavior
- [lean-dashboard](./lean-dashboard) - Qute, HTML, CSS for making simple and
fast dashboards
- [order-workflows](./order-workflows) - using Temporal for long running tasks
- [product-catalog](./product-catalog) - infinite scrolling using cursor-like
positioning instead of limit/offset "pages"
- [virtual-threads](./virtual-threads) - opting-in to Virtual Threads in Java
