package com.williamhsieh.financeapp.entity.payment;

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
    name = "payment_network_card_tiers",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_payment_network_card_tiers_network_tier",
            columnNames = {
                "payment_network_id",
                "card_tier_id"
            }
        )
    }
)
public class PaymentNetworkCardTier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "payment_network_id",
        nullable = false
    )
    private PaymentNetwork paymentNetwork;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_tier_id", nullable = false)
    private CardTier cardTier;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "level_rank", nullable = false)
    private int levelRank;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "note", length = 255)
    private String note;

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

    protected PaymentNetworkCardTier() {
    }

    public Long getId() {
        return id;
    }

    public PaymentNetwork getPaymentNetwork() {
        return paymentNetwork;
    }

    public void setPaymentNetwork(
        PaymentNetwork paymentNetwork
    ) {
        this.paymentNetwork = paymentNetwork;
    }

    public CardTier getCardTier() {
        return cardTier;
    }

    public void setCardTier(CardTier cardTier) {
        this.cardTier = cardTier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getLevelRank() {
        return levelRank;
    }

    public void setLevelRank(int levelRank) {
        this.levelRank = levelRank;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
