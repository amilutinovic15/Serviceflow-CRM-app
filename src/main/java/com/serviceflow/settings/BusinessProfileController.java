package com.serviceflow.settings;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings/business-profile")
public class BusinessProfileController {

    private final BusinessProfileService businessProfileService;

    public BusinessProfileController(BusinessProfileService businessProfileService) {
        this.businessProfileService = businessProfileService;
    }

    @GetMapping
    public BusinessProfileResponse getProfile() {
        return businessProfileService.getProfile();
    }

    @PutMapping
    public BusinessProfileResponse update(@Valid @RequestBody BusinessProfileRequest request) {
        return businessProfileService.update(request);
    }
}
