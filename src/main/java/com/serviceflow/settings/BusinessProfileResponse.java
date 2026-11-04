package com.serviceflow.settings;

import java.time.Instant;

public record BusinessProfileResponse(
        Long id,
        String businessName,
        String legalName,
        String taxNumber,
        String registrationNumber,
        String email,
        String phone,
        String website,
        String address,
        String city,
        String country,
        String currency,
        String invoiceFooter,
        Instant createdAt,
        Instant updatedAt
) {
    static BusinessProfileResponse from(BusinessProfile profile) {
        return new BusinessProfileResponse(
                profile.getId(),
                profile.getBusinessName(),
                profile.getLegalName(),
                profile.getTaxNumber(),
                profile.getRegistrationNumber(),
                profile.getEmail(),
                profile.getPhone(),
                profile.getWebsite(),
                profile.getAddress(),
                profile.getCity(),
                profile.getCountry(),
                profile.getCurrency(),
                profile.getInvoiceFooter(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
