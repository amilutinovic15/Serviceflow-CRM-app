package com.serviceflow.asset;

import java.time.Instant;
import java.time.LocalDate;

public record CustomerAssetResponse(
        Long id,
        Long customerId,
        String customerName,
        String name,
        String category,
        String assetIdentifier,
        String externalReference,
        String location,
        LocalDate serviceStartedAt,
        LocalDate lastServiceAt,
        LocalDate nextServiceDueAt,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
    static CustomerAssetResponse from(CustomerAsset asset) {
        return new CustomerAssetResponse(
                asset.getId(),
                asset.getCustomer().getId(),
                asset.getCustomer().getFullName(),
                asset.getName(),
                asset.getCategory(),
                asset.getAssetIdentifier(),
                asset.getExternalReference(),
                asset.getLocation(),
                asset.getServiceStartedAt(),
                asset.getLastServiceAt(),
                asset.getNextServiceDueAt(),
                asset.getNotes(),
                asset.getCreatedAt(),
                asset.getUpdatedAt()
        );
    }
}
