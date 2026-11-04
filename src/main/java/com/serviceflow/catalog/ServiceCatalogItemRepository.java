package com.serviceflow.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceCatalogItemRepository extends JpaRepository<ServiceCatalogItem, Long> {

    @Query("""
            select item
            from ServiceCatalogItem item
            where (:active is null or item.active = :active)
              and (
                  :search is null
                  or lower(item.name) like lower(concat('%', :search, '%'))
                  or lower(item.category) like lower(concat('%', :search, '%'))
                  or lower(item.description) like lower(concat('%', :search, '%'))
              )
            """)
    Page<ServiceCatalogItem> search(
            @Param("search") String search,
            @Param("active") Boolean active,
            Pageable pageable
    );
}
