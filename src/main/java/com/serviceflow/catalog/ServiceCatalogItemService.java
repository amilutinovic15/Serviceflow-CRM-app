package com.serviceflow.catalog;

import com.serviceflow.common.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceCatalogItemService {

    private final ServiceCatalogItemRepository serviceCatalogItemRepository;

    public ServiceCatalogItemService(ServiceCatalogItemRepository serviceCatalogItemRepository) {
        this.serviceCatalogItemRepository = serviceCatalogItemRepository;
    }

    @Transactional(readOnly = true)
    public Page<ServiceCatalogItemResponse> findAll(String search, Boolean active, Pageable pageable) {
        return serviceCatalogItemRepository.search(blankToNull(search), active, pageable)
                .map(ServiceCatalogItemResponse::from);
    }

    @Transactional(readOnly = true)
    public ServiceCatalogItemResponse findById(Long id) {
        return ServiceCatalogItemResponse.from(getItem(id));
    }

    public ServiceCatalogItemResponse create(ServiceCatalogItemRequest request) {
        ServiceCatalogItem item = new ServiceCatalogItem();
        applyRequest(item, request);
        return ServiceCatalogItemResponse.from(serviceCatalogItemRepository.save(item));
    }

    public ServiceCatalogItemResponse update(Long id, ServiceCatalogItemRequest request) {
        ServiceCatalogItem item = getItem(id);
        applyRequest(item, request);
        return ServiceCatalogItemResponse.from(item);
    }

    public ServiceCatalogItemResponse changeActive(Long id, boolean active) {
        ServiceCatalogItem item = getItem(id);
        item.setActive(active);
        return ServiceCatalogItemResponse.from(item);
    }

    public void delete(Long id) {
        serviceCatalogItemRepository.delete(getItem(id));
    }

    private ServiceCatalogItem getItem(Long id) {
        return serviceCatalogItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Catalog item with id %d was not found".formatted(id)));
    }

    private void applyRequest(ServiceCatalogItem item, ServiceCatalogItemRequest request) {
        item.setName(request.name().trim());
        item.setCategory(blankToNull(request.category()));
        item.setDescription(blankToNull(request.description()));
        item.setDefaultPrice(request.defaultPrice());
        item.setActive(request.active() == null || request.active());
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
