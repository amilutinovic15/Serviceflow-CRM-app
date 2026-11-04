package com.serviceflow.asset;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerAssetRepository extends JpaRepository<CustomerAsset, Long> {

    List<CustomerAsset> findByCustomerId(Long customerId);

    List<CustomerAsset> findByNextServiceDueAtBetween(LocalDate from, LocalDate to);

    @Query("""
            select a
            from CustomerAsset a
            where (:customerId is null or a.customer.id = :customerId)
              and (
                  :search is null
                  or lower(a.name) like lower(concat('%', :search, '%'))
                  or lower(a.category) like lower(concat('%', :search, '%'))
                  or lower(a.assetIdentifier) like lower(concat('%', :search, '%'))
                  or lower(a.externalReference) like lower(concat('%', :search, '%'))
              )
            """)
    Page<CustomerAsset> search(
            @Param("customerId") Long customerId,
            @Param("search") String search,
            Pageable pageable
    );
}
