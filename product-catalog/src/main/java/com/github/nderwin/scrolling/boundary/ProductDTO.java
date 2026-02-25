package com.github.nderwin.scrolling.boundary;

import io.quarkus.resteasy.reactive.links.RestLinkId;
import java.math.BigDecimal;

public record ProductDTO(
        @RestLinkId
        Long id,
        String name,
        String category,
        BigDecimal price,
        int viewCount,
        String createdAt,
        String cursor) {

}
