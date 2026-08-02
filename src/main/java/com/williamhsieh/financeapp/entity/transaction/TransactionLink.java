package com.williamhsieh.financeapp.entity.transaction;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "transaction_links",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_transaction_links_transaction_related",
            columnNames = {
                "transaction_id",
                "related_transaction_id"
            }
        )
    }
)
public class TransactionLink {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionEntry transaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "related_transaction_id",
        nullable = false
    )
    private TransactionEntry relatedTransaction;

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

    protected TransactionLink() {
    }

    public Long getId() {
        return id;
    }

    public TransactionEntry getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionEntry transaction) {
        this.transaction = transaction;
    }

    public TransactionEntry getRelatedTransaction() {
        return relatedTransaction;
    }

    public void setRelatedTransaction(
        TransactionEntry relatedTransaction
    ) {
        this.relatedTransaction = relatedTransaction;
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
