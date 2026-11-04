package com.serviceflow.asset;

import com.serviceflow.customer.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "customer_assets")
public class CustomerAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 80)
    private String category;

    @Column(name = "asset_identifier", length = 80)
    private String assetIdentifier;

    @Column(name = "external_reference", length = 120)
    private String externalReference;

    @Column(length = 160)
    private String location;

    @Column(name = "service_started_at")
    private LocalDate serviceStartedAt;

    @Column(name = "last_service_at")
    private LocalDate lastServiceAt;

    @Column(name = "next_service_due_at")
    private LocalDate nextServiceDueAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAssetIdentifier() {
        return assetIdentifier;
    }

    public void setAssetIdentifier(String assetIdentifier) {
        this.assetIdentifier = assetIdentifier;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getServiceStartedAt() {
        return serviceStartedAt;
    }

    public void setServiceStartedAt(LocalDate serviceStartedAt) {
        this.serviceStartedAt = serviceStartedAt;
    }

    public LocalDate getLastServiceAt() {
        return lastServiceAt;
    }

    public void setLastServiceAt(LocalDate lastServiceAt) {
        this.lastServiceAt = lastServiceAt;
    }

    public LocalDate getNextServiceDueAt() {
        return nextServiceDueAt;
    }

    public void setNextServiceDueAt(LocalDate nextServiceDueAt) {
        this.nextServiceDueAt = nextServiceDueAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
