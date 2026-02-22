package com.github.nderwin.virtual.threads.boundary;

import com.github.nderwin.virtual.threads.control.DatabaseSimulator;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("api/demo")
public class VirtualThreadResource {
    
    @Inject
    DatabaseSimulator sim;

    @GET
    @Path("hello")
    @Produces(TEXT_PLAIN)
    @RunOnVirtualThread
    public String hello() {
        return "Hello from: " + Thread.currentThread();
    }
    
    @GET
    @Path("query")
    @Produces(TEXT_PLAIN)
    @RunOnVirtualThread
    public String query() {
        final long start = System.currentTimeMillis();
        final String result = sim.queryDatabase("SELECT * FROM users");
        final long duration = System.currentTimeMillis() - start;
        
        return "Thread: " 
                + Thread.currentThread() 
                + "\nResult: " 
                + result 
                + "\nDuration: " 
                + duration 
                + "ms";
    }
    
    @GET
    @Path("multiple-queries")
    @Produces(APPLICATION_JSON)
    @RunOnVirtualThread
    public List<String> multipleQueries() {
        return IntStream.range(0, 5)
                .mapToObj(i -> sim.queryDatabase("Query " + i))
                .toList();
    }
    
    @GET
    @Path("parallel/{count}")
    @Produces(APPLICATION_JSON)
    @RunOnVirtualThread
    public Map<String, Object> parallel(
            @PathParam("count")
            final int count
    ) {
        final long start = System.currentTimeMillis();
        
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = IntStream.range(0, count)
                    .mapToObj(i -> executor.submit(() -> sim.slowQuery(1000)))
                    .toList();
            
            var results = futures.stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (final Exception ex) {
                            return "Error: " + ex.getMessage();
                        }
                    })
                    .toList();
            
            final long duration = System.currentTimeMillis() - start;
            return Map.of(
                    "count", count,
                    "duration_ms", duration,
                    "results", results
            );
        }
    }

    @GET
    @Path("info")
    @Produces(APPLICATION_JSON)
    public Map<String, Object> info() {
        final var t = Thread.currentThread();
        return Map.of(
                "thread", t.toString(),
                "is_virtual", t.isVirtual(),
                "active_threads", Thread.activeCount()
        );
    }
    
    @GET
    @Path("info/virtual")
    @Produces(APPLICATION_JSON)
    @RunOnVirtualThread
    public Map<String, Object> virtualInfo() {
        final var t = Thread.currentThread();
        return Map.of(
                "thread", t.toString(),
                "is_virtual", t.isVirtual(),
                "active_threads", Thread.activeCount()
        );
    }
    
    @GET
    @Path("load-test/{n}")
    @Produces(APPLICATION_JSON)
    @RunOnVirtualThread
    public Map<String, Object> load(
            @PathParam("n")
            final int n
    ) {
        final long start = System.currentTimeMillis();
        
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var tasks = IntStream.range(0, n)
                    .mapToObj(i -> executor.submit(() -> sim.slowQuery(100)))
                    .toList();
            
            final long done = tasks.stream().filter(f -> {
                try {
                    f.get();
                    return true;
                } catch (final Exception ex) {
                    return false;
                }
            }).count();
            
            final long duration = System.currentTimeMillis() - start;
            return Map.of(
                    "requested", n,
                    "completed", done,
                    "duration_ms", duration,
                    "rps", (n * 1000.0) / duration
            );
        }
    }
}
