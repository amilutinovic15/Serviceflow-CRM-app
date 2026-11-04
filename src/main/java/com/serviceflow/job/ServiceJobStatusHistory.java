package com.serviceflow.job;

import com.serviceflow.user.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "service_job_status_history")
public class ServiceJobStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_job_id", nullable = false)
    private ServiceJob serviceJob;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 40)
    private ServiceJobStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 40)
    private ServiceJobStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_user_id")
    private AppUser changedByUser;

    @Column(name = "changed_by_email", length = 160)
    private String changedByEmail;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private Instant changedAt;

    @PrePersist
    void onCreate() {
        changedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public ServiceJob getServiceJob() {
        return serviceJob;
    }

    public void setServiceJob(ServiceJob serviceJob) {
        this.serviceJob = serviceJob;
    }

    public ServiceJobStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(ServiceJobStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public ServiceJobStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ServiceJobStatus newStatus) {
        this.newStatus = newStatus;
    }

    public AppUser getChangedByUser() {
        return changedByUser;
    }

    public void setChangedByUser(AppUser changedByUser) {
        this.changedByUser = changedByUser;
    }

    public String getChangedByEmail() {
        return changedByEmail;
    }

    public void setChangedByEmail(String changedByEmail) {
        this.changedByEmail = changedByEmail;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getChangedAt() {
        return changedAt;
    }
}
