package com.serviceflow.asset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CustomerAssetRequest(
        @NotNull Long customerId,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 80) String category,
        @Size(max = 80) String assetIdentifier,
        @Size(max = 120) String externalReference,
        @Size(max = 160) String location,
        LocalDate serviceStartedAt,
        LocalDate lastServiceAt,
        LocalDate nextServiceDueAt,
        String notes
) {
}
