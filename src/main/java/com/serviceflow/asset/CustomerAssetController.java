package com.serviceflow.asset;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assets")
public class CustomerAssetController {

    private final CustomerAssetService customerAssetService;

    public CustomerAssetController(CustomerAssetService customerAssetService) {
        this.customerAssetService = customerAssetService;
    }

    @GetMapping
    public Page<CustomerAssetResponse> findAll(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String search,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return customerAssetService.findAll(customerId, search, pageable);
    }

    @GetMapping("/due-soon")
    public List<CustomerAssetResponse> findDueSoon(@RequestParam(defaultValue = "30") int days) {
        return customerAssetService.findDueSoon(days);
    }

    @GetMapping("/{id}")
    public CustomerAssetResponse findById(@PathVariable Long id) {
        return customerAssetService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CustomerAssetResponse> create(@Valid @RequestBody CustomerAssetRequest request) {
        CustomerAssetResponse response = customerAssetService.create(request);
        return ResponseEntity
                .created(URI.create("/api/assets/" + response.id()))
                .body(response);
    }

    @PutMapping("/{id}")
    public CustomerAssetResponse update(@PathVariable Long id, @Valid @RequestBody CustomerAssetRequest request) {
        return customerAssetService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerAssetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
