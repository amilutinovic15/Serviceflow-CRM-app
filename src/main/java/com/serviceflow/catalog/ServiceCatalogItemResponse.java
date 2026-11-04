package com.serviceflow.catalog;

import java.math.BigDecimal;
import java.time.Instant;

public record ServiceCatalogItemResponse(
        Long id,
        String name,
        String category,
        String description,
        BigDecimal defaultPrice,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static ServiceCatalogItemResponse from(ServiceCatalogItem item) {
        return new ServiceCatalogItemResponse(
                item.getId(),
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDefaultPrice(),
                item.isActive(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
