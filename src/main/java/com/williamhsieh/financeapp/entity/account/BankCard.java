package com.williamhsieh.financeapp.entity.account;

import java.time.LocalDate;

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

@Entity
@Table(name = "bank_cards")
public class BankCard {

    @Id
    private Long accountId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount bankAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_network_card_tier_id")
    private PaymentNetworkCardTier paymentNetworkCardTier;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "card_number", length = 20)
    private String cardNumber;

    @Column(name = "card_holder_name", length = 100)
    private String cardHolderName;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "is_virtual", nullable = false)
    private boolean virtual = false;

    @Column(name = "note", length = 255)
    private String note;

    protected BankCard() {
    }

    public Long getAccountId() {
        return accountId;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public PaymentNetworkCardTier getPaymentNetworkCardTier() {
        return paymentNetworkCardTier;
    }

    public void setPaymentNetworkCardTier(
        PaymentNetworkCardTier paymentNetworkCardTier
    ) {
        this.paymentNetworkCardTier = paymentNetworkCardTier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
