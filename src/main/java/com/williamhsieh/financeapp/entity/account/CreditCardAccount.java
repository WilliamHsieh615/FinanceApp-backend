package com.williamhsieh.financeapp.entity.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.williamhsieh.financeapp.entity.institution.FinancialInstitution;
import com.williamhsieh.financeapp.entity.payment.PaymentNetworkCardTier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "credit_card_accounts",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_credit_card_accounts_account_auto_pay",
            columnNames = {
                "account_id",
                "auto_pay_account_id"
            }
        )
    }
)
public class CreditCardAccount {
    
    @Id
    private Long accountId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_institution_id")
    private FinancialInstitution financialInstitution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_network_card_tier_id")
    private PaymentNetworkCardTier paymentNetworkCardTier;

    @Column(name = "card_number", length = 20)
    private String cardNumber;

    @Column(name = "card_holder_name", length = 100)
    private String cardHolderName;

    @Column(
        name = "credit_limit",
        precision = 18,
        scale = 8
    )
    private BigDecimal creditLimit;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "is_virtual", nullable = false)
    private boolean virtual = false;

    @Column(name = "cycle_day", nullable = false)
    private byte cycleDay;

    @Column(name = "due_day", nullable = false)
    private byte dueDay;

    @Column(
        name = "annual_fee",
        precision = 18,
        scale = 8
    )
    private BigDecimal annualFee;

    @Column(name = "is_auto_pay")
    private Boolean autoPay = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auto_pay_account_id")
    private Account autoPayAccount;

    protected CreditCardAccount() {
    }

    public Long getAccountId() {
        return accountId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public FinancialInstitution getFinancialInstitution() {
        return financialInstitution;
    }

    public void setFinancialInstitution(
        FinancialInstitution financialInstitution
    ) {
        this.financialInstitution = financialInstitution;
    }

    public PaymentNetworkCardTier getPaymentNetworkCardTier() {
        return paymentNetworkCardTier;
    }

    public void setPaymentNetworkCardTier(
        PaymentNetworkCardTier paymentNetworkCardTier
    ) {
        this.paymentNetworkCardTier = paymentNetworkCardTier;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public byte getCycleDay() {
        return cycleDay;
    }

    public void setCycleDay(byte cycleDay) {
        this.cycleDay = cycleDay;
    }

    public byte getDueDay() {
        return dueDay;
    }

    public void setDueDay(byte dueDay) {
        this.dueDay = dueDay;
    }

    public BigDecimal getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(BigDecimal annualFee) {
        this.annualFee = annualFee;
    }

    public Boolean getAutoPay() {
        return autoPay;
    }

    public void setAutoPay(Boolean autoPay) {
        this.autoPay = autoPay;
    }

    public Account getAutoPayAccount() {
        return autoPayAccount;
    }

    public void setAutoPayAccount(Account autoPayAccount) {
        this.autoPayAccount = autoPayAccount;
    }
}
