package com.serviceflow.job;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceJobRepository extends JpaRepository<ServiceJob, Long> {

    List<ServiceJob> findByCustomerId(Long customerId);

    List<ServiceJob> findByStatus(ServiceJobStatus status);

    long countByStatus(ServiceJobStatus status);

    long countByPriority(ServiceJobPriority priority);

    Page<ServiceJob> findByScheduledAtBetweenAndStatusIn(
            LocalDateTime from,
            LocalDateTime to,
            List<ServiceJobStatus> statuses,
            Pageable pageable
    );

    Page<ServiceJob> findByCustomerId(Long customerId, Pageable pageable);

    Page<ServiceJob> findByStatus(ServiceJobStatus status, Pageable pageable);

    Page<ServiceJob> findByPriority(ServiceJobPriority priority, Pageable pageable);

    Page<ServiceJob> findByCustomerIdAndStatus(Long customerId, ServiceJobStatus status, Pageable pageable);

    Page<ServiceJob> findByCustomerIdAndPriority(Long customerId, ServiceJobPriority priority, Pageable pageable);

    Page<ServiceJob> findByStatusAndPriority(ServiceJobStatus status, ServiceJobPriority priority, Pageable pageable);

    Page<ServiceJob> findByCustomerIdAndStatusAndPriority(
            Long customerId,
            ServiceJobStatus status,
            ServiceJobPriority priority,
            Pageable pageable
    );

    Page<ServiceJob> findByAssigneeId(Long assigneeId, Pageable pageable);

    Page<ServiceJob> findByCustomerIdAndAssigneeId(Long customerId, Long assigneeId, Pageable pageable);

    Page<ServiceJob> findByStatusAndAssigneeId(ServiceJobStatus status, Long assigneeId, Pageable pageable);

    Page<ServiceJob> findByPriorityAndAssigneeId(ServiceJobPriority priority, Long assigneeId, Pageable pageable);

    Page<ServiceJob> findByCustomerIdAndPriorityAndAssigneeId(
            Long customerId,
            ServiceJobPriority priority,
            Long assigneeId,
            Pageable pageable
    );

    Page<ServiceJob> findByStatusAndPriorityAndAssigneeId(
            ServiceJobStatus status,
            ServiceJobPriority priority,
            Long assigneeId,
            Pageable pageable
    );

    Page<ServiceJob> findByCustomerIdAndStatusAndAssigneeId(
            Long customerId,
            ServiceJobStatus status,
            Long assigneeId,
            Pageable pageable
    );

    Page<ServiceJob> findByCustomerIdAndStatusAndPriorityAndAssigneeId(
            Long customerId,
            ServiceJobStatus status,
            ServiceJobPriority priority,
            Long assigneeId,
            Pageable pageable
    );
}
