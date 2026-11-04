package com.serviceflow.asset;

import com.serviceflow.common.NotFoundException;
import com.serviceflow.customer.Customer;
import com.serviceflow.customer.CustomerRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerAssetService {

    private final CustomerAssetRepository customerAssetRepository;
    private final CustomerRepository customerRepository;

    public CustomerAssetService(CustomerAssetRepository customerAssetRepository, CustomerRepository customerRepository) {
        this.customerAssetRepository = customerAssetRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public Page<CustomerAssetResponse> findAll(Long customerId, String search, Pageable pageable) {
        return customerAssetRepository.search(customerId, blankToNull(search), pageable)
                .map(CustomerAssetResponse::from);
    }

    @Transactional(readOnly = true)
    public List<CustomerAssetResponse> findDueSoon(int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Days must be zero or greater");
        }

        LocalDate today = LocalDate.now();
        return customerAssetRepository.findByNextServiceDueAtBetween(today, today.plusDays(days))
                .stream()
                .map(CustomerAssetResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerAssetResponse findById(Long id) {
        return CustomerAssetResponse.from(getAsset(id));
    }

    public CustomerAssetResponse create(CustomerAssetRequest request) {
        CustomerAsset asset = new CustomerAsset();
        applyRequest(asset, request);
        return CustomerAssetResponse.from(customerAssetRepository.save(asset));
    }

    public CustomerAssetResponse update(Long id, CustomerAssetRequest request) {
        CustomerAsset asset = getAsset(id);
        applyRequest(asset, request);
        return CustomerAssetResponse.from(asset);
    }

    public void delete(Long id) {
        customerAssetRepository.delete(getAsset(id));
    }

    private CustomerAsset getAsset(Long id) {
        return customerAssetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Asset with id %d was not found".formatted(id)));
    }

    private void applyRequest(CustomerAsset asset, CustomerAssetRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new NotFoundException("Customer with id %d was not found".formatted(request.customerId())));

        asset.setCustomer(customer);
        asset.setName(request.name().trim());
        asset.setCategory(blankToNull(request.category()));
        asset.setAssetIdentifier(blankToNull(request.assetIdentifier()));
        asset.setExternalReference(blankToNull(request.externalReference()));
        asset.setLocation(blankToNull(request.location()));
        asset.setServiceStartedAt(request.serviceStartedAt());
        asset.setLastServiceAt(request.lastServiceAt());
        asset.setNextServiceDueAt(request.nextServiceDueAt());
        asset.setNotes(blankToNull(request.notes()));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
