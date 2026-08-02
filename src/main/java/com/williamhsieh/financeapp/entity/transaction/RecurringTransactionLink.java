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
    name = "recurring_transaction_links",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_recurring_transaction_links_pair",
            columnNames = {
                "recurring_transaction_id",
                "related_recurring_transaction_id"
            }
        )
    }
)
public class RecurringTransactionLink {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "recurring_transaction_id",
        nullable = false
    )
    private RecurringTransactionEntry recurringTransaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "related_recurring_transaction_id",
        nullable = false
    )
    private RecurringTransactionEntry relatedRecurringTransaction;

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

    protected RecurringTransactionLink() {
    }

    public Long getId() {
        return id;
    }

    public RecurringTransactionEntry getRecurringTransaction() {
        return recurringTransaction;
    }

    public void setRecurringTransaction(
        RecurringTransactionEntry recurringTransaction
    ) {
        this.recurringTransaction = recurringTransaction;
    }

    public RecurringTransactionEntry getRelatedRecurringTransaction() {
        return relatedRecurringTransaction;
    }

    public void setRelatedRecurringTransaction(
        RecurringTransactionEntry relatedRecurringTransaction
    ) {
        this.relatedRecurringTransaction =
            relatedRecurringTransaction;
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
