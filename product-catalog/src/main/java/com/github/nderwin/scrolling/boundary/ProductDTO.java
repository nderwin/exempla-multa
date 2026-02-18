package com.github.nderwin.scrolling.boundary;

import java.math.BigDecimal;

public record ProductDTO(
        Long id,
        String name,
        String category,
        BigDecimal price,
        int viewCount,
        String createdAt,
        String cursor) {

}
