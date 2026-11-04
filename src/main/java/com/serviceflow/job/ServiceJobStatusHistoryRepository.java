package com.serviceflow.job;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceJobStatusHistoryRepository extends JpaRepository<ServiceJobStatusHistory, Long> {

    List<ServiceJobStatusHistory> findByServiceJobIdOrderByChangedAtDesc(Long serviceJobId);
}
