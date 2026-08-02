package com.williamhsieh.financeapp.entity.transaction;

import com.williamhsieh.financeapp.entity.category.Category;
import com.williamhsieh.financeapp.entity.merchant.Merchant;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "cashflow_transaction_details",
    indexes = {
        @Index(
            name = "idx_cashflow_transaction_details_category",
            columnList = "category_id"
        )
    }
)
public class CashflowTransactionDetail {

    @Id
    private Long transactionId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionEntry transaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    protected CashflowTransactionDetail() {
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public TransactionEntry getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionEntry transaction) {
        this.transaction = transaction;
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
