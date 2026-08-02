package com.williamhsieh.financeapp.entity.transaction;

import com.williamhsieh.financeapp.entity.category.Category;
import com.williamhsieh.financeapp.entity.merchant.Merchant;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cashflow_recurring_transaction_details")
public class CashflowRecurringTransactionDetail {
    
    @Id
    private Long recurringTransactionId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(
        name = "recurring_transaction_id",
        nullable = false
    )
    private RecurringTransactionEntry recurringTransaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    protected CashflowRecurringTransactionDetail() {
    }

    public Long getRecurringTransactionId() {
        return recurringTransactionId;
    }

    public RecurringTransactionEntry getRecurringTransaction() {
        return recurringTransaction;
    }

    public void setRecurringTransaction(
        RecurringTransactionEntry recurringTransaction
    ) {
        this.recurringTransaction = recurringTransaction;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }
}
