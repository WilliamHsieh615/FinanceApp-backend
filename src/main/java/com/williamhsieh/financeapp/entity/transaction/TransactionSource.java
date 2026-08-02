package com.williamhsieh.financeapp.entity.transaction;

import java.time.LocalDateTime;

import com.williamhsieh.financeapp.entity.metadata.EntityType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction_sources")
public class TransactionSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "transaction_source_type_id",
        nullable = false
    )
    private TransactionSourceType transactionSourceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_type_id")
    private EntityType sourceType;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(
        name = "created_date",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private LocalDateTime createdDate;

    @Column(
        name = "updated_date",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private LocalDateTime updatedDate;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    protected TransactionSource() {
    }

    public Long getId() {
        return id;
    }

    public TransactionSourceType getTransactionSourceType() {
        return transactionSourceType;
    }

    public void setTransactionSourceType(
        TransactionSourceType transactionSourceType
    ) {
        this.transactionSourceType = transactionSourceType;
    }

    public EntityType getSourceType() {
        return sourceType;
    }

    public void setSourceType(EntityType sourceType) {
        this.sourceType = sourceType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public LocalDateTime getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(LocalDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }
}
