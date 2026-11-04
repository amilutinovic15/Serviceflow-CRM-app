package com.serviceflow.settings;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BusinessProfileService {

    private final BusinessProfileRepository businessProfileRepository;

    public BusinessProfileService(BusinessProfileRepository businessProfileRepository) {
        this.businessProfileRepository = businessProfileRepository;
    }

    @Transactional(readOnly = true)
    public BusinessProfile getProfileEntity() {
        return businessProfileRepository.findById(BusinessProfile.DEFAULT_ID)
                .orElseGet(this::defaultProfile);
    }

    @Transactional(readOnly = true)
    public BusinessProfileResponse getProfile() {
        return BusinessProfileResponse.from(getProfileEntity());
    }

    public BusinessProfileResponse update(BusinessProfileRequest request) {
        BusinessProfile profile = businessProfileRepository.findById(BusinessProfile.DEFAULT_ID)
                .orElseGet(this::defaultProfile);

        profile.setBusinessName(request.businessName().trim());
        profile.setLegalName(blankToNull(request.legalName()));
        profile.setTaxNumber(blankToNull(request.taxNumber()));
        profile.setRegistrationNumber(blankToNull(request.registrationNumber()));
        profile.setEmail(blankToNull(request.email()));
        profile.setPhone(blankToNull(request.phone()));
        profile.setWebsite(blankToNull(request.website()));
        profile.setAddress(blankToNull(request.address()));
        profile.setCity(blankToNull(request.city()));
        profile.setCountry(blankToNull(request.country()));
        profile.setCurrency(request.currency().trim().toUpperCase());
        profile.setInvoiceFooter(blankToNull(request.invoiceFooter()));

        return BusinessProfileResponse.from(businessProfileRepository.save(profile));
    }

    private BusinessProfile defaultProfile() {
        BusinessProfile profile = new BusinessProfile();
        profile.setBusinessName("ServiceFlow Demo Business");
        profile.setLegalName("ServiceFlow Demo Business");
        profile.setEmail("office@serviceflow.local");
        profile.setPhone("+381641234567");
        profile.setAddress("Kragujevačka 3");
        profile.setCity("Smederevo");
        profile.setCountry("Serbia");
        profile.setCurrency("RSD");
        profile.setInvoiceFooter("Thank you for your business.");
        return profile;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
