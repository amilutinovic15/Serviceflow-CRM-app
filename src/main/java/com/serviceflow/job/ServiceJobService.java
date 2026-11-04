package com.serviceflow.job;

import com.serviceflow.common.NotFoundException;
import com.serviceflow.catalog.ServiceCatalogItem;
import com.serviceflow.catalog.ServiceCatalogItemRepository;
import com.serviceflow.customer.Customer;
import com.serviceflow.customer.CustomerRepository;
import com.serviceflow.user.AppUser;
import com.serviceflow.user.AppUserRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceJobService {

    private final ServiceJobRepository serviceJobRepository;
    private final CustomerRepository customerRepository;
    private final ServiceJobStatusHistoryRepository statusHistoryRepository;
    private final AppUserRepository appUserRepository;
    private final ServiceCatalogItemRepository serviceCatalogItemRepository;

    public ServiceJobService(
            ServiceJobRepository serviceJobRepository,
            CustomerRepository customerRepository,
            ServiceJobStatusHistoryRepository statusHistoryRepository,
            AppUserRepository appUserRepository,
            ServiceCatalogItemRepository serviceCatalogItemRepository
    ) {
        this.serviceJobRepository = serviceJobRepository;
        this.customerRepository = customerRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.appUserRepository = appUserRepository;
        this.serviceCatalogItemRepository = serviceCatalogItemRepository;
    }

    @Transactional(readOnly = true)
    public Page<ServiceJobResponse> findAll(
            Long customerId,
            ServiceJobStatus status,
            ServiceJobPriority priority,
            Long assigneeId,
            Pageable pageable
    ) {
        Page<ServiceJob> jobs;

        if (customerId != null && status != null && priority != null && assigneeId != null) {
            jobs = serviceJobRepository.findByCustomerIdAndStatusAndPriorityAndAssigneeId(
                    customerId, status, priority, assigneeId, pageable);
        } else if (customerId != null && status != null && priority != null) {
            jobs = serviceJobRepository.findByCustomerIdAndStatusAndPriority(customerId, status, priority, pageable);
        } else if (customerId != null && status != null && assigneeId != null) {
            jobs = serviceJobRepository.findByCustomerIdAndStatusAndAssigneeId(customerId, status, assigneeId, pageable);
        } else if (customerId != null && priority != null && assigneeId != null) {
            jobs = serviceJobRepository.findByCustomerIdAndPriorityAndAssigneeId(customerId, priority, assigneeId, pageable);
        } else if (status != null && priority != null && assigneeId != null) {
            jobs = serviceJobRepository.findByStatusAndPriorityAndAssigneeId(status, priority, assigneeId, pageable);
        } else if (customerId != null && status != null) {
            jobs = serviceJobRepository.findByCustomerIdAndStatus(customerId, status, pageable);
        } else if (customerId != null && priority != null) {
            jobs = serviceJobRepository.findByCustomerIdAndPriority(customerId, priority, pageable);
        } else if (customerId != null && assigneeId != null) {
            jobs = serviceJobRepository.findByCustomerIdAndAssigneeId(customerId, assigneeId, pageable);
        } else if (status != null && priority != null) {
            jobs = serviceJobRepository.findByStatusAndPriority(status, priority, pageable);
        } else if (status != null && assigneeId != null) {
            jobs = serviceJobRepository.findByStatusAndAssigneeId(status, assigneeId, pageable);
        } else if (priority != null && assigneeId != null) {
            jobs = serviceJobRepository.findByPriorityAndAssigneeId(priority, assigneeId, pageable);
        } else if (customerId != null) {
            jobs = serviceJobRepository.findByCustomerId(customerId, pageable);
        } else if (status != null) {
            jobs = serviceJobRepository.findByStatus(status, pageable);
        } else if (priority != null) {
            jobs = serviceJobRepository.findByPriority(priority, pageable);
        } else if (assigneeId != null) {
            jobs = serviceJobRepository.findByAssigneeId(assigneeId, pageable);
        } else {
            jobs = serviceJobRepository.findAll(pageable);
        }

        return jobs.map(ServiceJobResponse::from);
    }

    @Transactional(readOnly = true)
    public ServiceJobResponse findById(Long id) {
        return ServiceJobResponse.from(getJob(id));
    }

    public ServiceJobResponse create(ServiceJobRequest request) {
        ServiceJob job = new ServiceJob();
        applyRequest(job, request);
        ServiceJob savedJob = serviceJobRepository.save(job);
        recordStatusChange(savedJob, null, savedJob.getStatus(), "Job created");
        return ServiceJobResponse.from(savedJob);
    }

    public ServiceJobResponse update(Long id, ServiceJobRequest request) {
        ServiceJob job = getJob(id);
        ServiceJobStatus previousStatus = job.getStatus();
        applyRequest(job, request);
        if (previousStatus != job.getStatus()) {
            recordStatusChange(job, previousStatus, job.getStatus(), "Job updated");
        }
        return ServiceJobResponse.from(job);
    }

    public ServiceJobResponse changeStatus(Long id, ServiceJobStatus status, String note) {
        ServiceJob job = getJob(id);
        ServiceJobStatus previousStatus = job.getStatus();
        job.setStatus(status);
        if (previousStatus != status) {
            recordStatusChange(job, previousStatus, status, note);
        }
        return ServiceJobResponse.from(job);
    }

    @Transactional(readOnly = true)
    public List<ServiceJobStatusHistoryResponse> findHistory(Long id) {
        if (!serviceJobRepository.existsById(id)) {
            throw new NotFoundException("Service job with id %d was not found".formatted(id));
        }

        return statusHistoryRepository.findByServiceJobIdOrderByChangedAtDesc(id)
                .stream()
                .map(ServiceJobStatusHistoryResponse::from)
                .toList();
    }

    public void delete(Long id) {
        serviceJobRepository.delete(getJob(id));
    }

    private ServiceJob getJob(Long id) {
        return serviceJobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service job with id %d was not found".formatted(id)));
    }

    private void applyRequest(ServiceJob job, ServiceJobRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new NotFoundException("Customer with id %d was not found".formatted(request.customerId())));
        AppUser assignee = resolveAssignee(request.assigneeId());
        ServiceCatalogItem catalogItem = resolveCatalogItem(request.catalogItemId());

        job.setCustomer(customer);
        job.setAssignee(assignee);
        job.setCatalogItem(catalogItem);
        job.setTitle(request.title().trim());
        job.setDescription(blankToNull(request.description()));
        job.setStatus(request.status() == null ? ServiceJobStatus.NEW : request.status());
        job.setPriority(request.priority() == null ? ServiceJobPriority.NORMAL : request.priority());
        job.setScheduledAt(request.scheduledAt());
        job.setCompletedAt(request.completedAt());
        job.setPrice(resolvePrice(request, catalogItem));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private AppUser resolveAssignee(Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }

        return appUserRepository.findById(assigneeId)
                .orElseThrow(() -> new NotFoundException("User with id %d was not found".formatted(assigneeId)));
    }

    private ServiceCatalogItem resolveCatalogItem(Long catalogItemId) {
        if (catalogItemId == null) {
            return null;
        }

        ServiceCatalogItem catalogItem = serviceCatalogItemRepository.findById(catalogItemId)
                .orElseThrow(() -> new NotFoundException("Catalog item with id %d was not found".formatted(catalogItemId)));
        if (!catalogItem.isActive()) {
            throw new IllegalArgumentException("Catalog item with id %d is inactive".formatted(catalogItemId));
        }

        return catalogItem;
    }

    private java.math.BigDecimal resolvePrice(ServiceJobRequest request, ServiceCatalogItem catalogItem) {
        if (request.price() != null) {
            return request.price();
        }

        return catalogItem == null ? null : catalogItem.getDefaultPrice();
    }

    private void recordStatusChange(
            ServiceJob job,
            ServiceJobStatus previousStatus,
            ServiceJobStatus newStatus,
            String note
    ) {
        String email = currentUserEmail();
        AppUser user = email == null ? null : appUserRepository.findByEmail(email).orElse(null);

        ServiceJobStatusHistory history = new ServiceJobStatusHistory();
        history.setServiceJob(job);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setChangedByUser(user);
        history.setChangedByEmail(email);
        history.setNote(blankToNull(note));

        statusHistoryRepository.save(history);
    }

    private String currentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String name = authentication.getName();
        return name == null || "anonymousUser".equals(name) ? null : name;
    }
}
