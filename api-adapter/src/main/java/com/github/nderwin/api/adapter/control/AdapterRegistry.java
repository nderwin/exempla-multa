package com.github.nderwin.api.adapter.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AdapterRegistry {
    
    private final List<RequestAdapter<?>> requestAdapters;
    
    private final List<ResponseAdapter<?>> responseAdapters;

    public AdapterRegistry(
            Instance<RequestAdapter<?>> requestAdapters, 
            Instance<ResponseAdapter<?>> responseAdapters
    ) {
        this.requestAdapters = requestAdapters.stream().toList();
        this.responseAdapters = responseAdapters.stream().toList();
    }
    
    public <T> RequestAdapter<T> requestAdapterFor(final String version, final Class<T> targetTypeHint) {
        final LocalDate ld = LocalDate.parse(version);
        @SuppressWarnings("unchecked")
        final Optional<RequestAdapter<T>> best = (Optional<RequestAdapter<T>>) (Optional<?>) requestAdapters.stream()
                .filter(a -> LocalDate.parse(a.version()).compareTo(ld) <= 0)
                .sorted(Comparator.comparing(a -> LocalDate.parse(a.version()), Comparator.reverseOrder()))
                .findFirst();
        
        return best.orElseThrow(() -> new IllegalArgumentException("No RequestAdapter for version " + version));
    }
    
    public <R> ResponseAdapter<R> responseAdapterFor(final String version, final Class<R> targetTypeHint) {
        final LocalDate ld = LocalDate.parse(version);
        @SuppressWarnings("unchecked")
        final Optional<ResponseAdapter<R>> best = (Optional<ResponseAdapter<R>>) (Optional<?>) responseAdapters.stream()
                .filter(a -> LocalDate.parse(a.version()).compareTo(ld) <= 0)
                .sorted(Comparator.comparing(a -> LocalDate.parse(a.version()), Comparator.reverseOrder()))
                .findFirst();
        
        return best.orElseThrow(() -> new IllegalArgumentException("No ResponseAdapter for version " + version));
    }
    
}
