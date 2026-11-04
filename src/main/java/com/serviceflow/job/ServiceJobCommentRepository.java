package com.serviceflow.job;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceJobCommentRepository extends JpaRepository<ServiceJobComment, Long> {

    List<ServiceJobComment> findByServiceJobIdOrderByCreatedAtDesc(Long serviceJobId);

    List<ServiceJobComment> findByServiceJobCustomerId(Long customerId);
}
