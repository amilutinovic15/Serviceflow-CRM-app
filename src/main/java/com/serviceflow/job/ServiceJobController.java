package com.serviceflow.job;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

@Validated
@RestController
@RequestMapping("/api/jobs")
public class ServiceJobController {

    private final ServiceJobService serviceJobService;
    private final ServiceJobCommentService serviceJobCommentService;

    public ServiceJobController(ServiceJobService serviceJobService, ServiceJobCommentService serviceJobCommentService) {
        this.serviceJobService = serviceJobService;
        this.serviceJobCommentService = serviceJobCommentService;
    }

    @GetMapping
    public Page<ServiceJobResponse> findAll(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) ServiceJobStatus status,
            @RequestParam(required = false) ServiceJobPriority priority,
            @RequestParam(required = false) Long assigneeId,
            @ParameterObject @PageableDefault(size = 20, sort = "scheduledAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return serviceJobService.findAll(customerId, status, priority, assigneeId, pageable);
    }

    @GetMapping("/{id}")
    public ServiceJobResponse findById(@PathVariable Long id) {
        return serviceJobService.findById(id);
    }

    @GetMapping("/{id}/history")
    public List<ServiceJobStatusHistoryResponse> findHistory(@PathVariable Long id) {
        return serviceJobService.findHistory(id);
    }

    @GetMapping("/{id}/comments")
    public List<ServiceJobCommentResponse> findComments(@PathVariable Long id) {
        return serviceJobCommentService.findByJobId(id);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ServiceJobCommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody ServiceJobCommentRequest request
    ) {
        ServiceJobCommentResponse response = serviceJobCommentService.addComment(id, request);
        return ResponseEntity
                .created(URI.create("/api/jobs/" + id + "/comments/" + response.id()))
                .body(response);
    }

    @PostMapping
    public ResponseEntity<ServiceJobResponse> create(@Valid @RequestBody ServiceJobRequest request) {
        ServiceJobResponse response = serviceJobService.create(request);
        return ResponseEntity
                .created(URI.create("/api/jobs/" + response.id()))
                .body(response);
    }

    @PutMapping("/{id}")
    public ServiceJobResponse update(@PathVariable Long id, @Valid @RequestBody ServiceJobRequest request) {
        return serviceJobService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public ServiceJobResponse changeStatus(
            @PathVariable Long id,
            @RequestParam @NotNull ServiceJobStatus status,
            @RequestParam(required = false) String note
    ) {
        return serviceJobService.changeStatus(id, status, note);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceJobService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @PathVariable Long commentId) {
        serviceJobCommentService.deleteComment(id, commentId);
        return ResponseEntity.noContent().build();
    }
}
