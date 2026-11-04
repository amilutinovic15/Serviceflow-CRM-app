package com.serviceflow.catalog;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/items")
public class ServiceCatalogItemController {

    private final ServiceCatalogItemService serviceCatalogItemService;

    public ServiceCatalogItemController(ServiceCatalogItemService serviceCatalogItemService) {
        this.serviceCatalogItemService = serviceCatalogItemService;
    }

    @GetMapping
    public Page<ServiceCatalogItemResponse> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return serviceCatalogItemService.findAll(search, active, pageable);
    }

    @GetMapping("/{id}")
    public ServiceCatalogItemResponse findById(@PathVariable Long id) {
        return serviceCatalogItemService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ServiceCatalogItemResponse> create(@Valid @RequestBody ServiceCatalogItemRequest request) {
        ServiceCatalogItemResponse response = serviceCatalogItemService.create(request);
        return ResponseEntity
                .created(URI.create("/api/catalog/items/" + response.id()))
                .body(response);
    }

    @PutMapping("/{id}")
    public ServiceCatalogItemResponse update(@PathVariable Long id, @Valid @RequestBody ServiceCatalogItemRequest request) {
        return serviceCatalogItemService.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public ServiceCatalogItemResponse changeActive(@PathVariable Long id, @RequestParam boolean active) {
        return serviceCatalogItemService.changeActive(id, active);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceCatalogItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
