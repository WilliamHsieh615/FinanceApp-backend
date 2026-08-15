package com.williamhsieh.financeapp.entity.user;

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

@Entity
@Table(name = "user_sms_verifications")
public class UserSmsVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "code_hash", nullable = false, length = 255)
    private String codeHash;

    @Column(name = "expires_date", nullable = false)
    private LocalDateTime expiresDate;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount = 0;

    @Column(name = "verified_date")
    private LocalDateTime verifiedDate;

    @Column(
        name = "created_date",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private LocalDateTime createdDate;

    public UserSmsVerification() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public void setCodeHash(String codeHash) {
        this.codeHash = codeHash;
    }

    public LocalDateTime getExpiresDate() {
        return expiresDate;
    }

    public void setExpiresDate(LocalDateTime expiresDate) {
        this.expiresDate = expiresDate;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public LocalDateTime getVerifiedDate() {
        return verifiedDate;
    }

    public void setVerifiedDate(LocalDateTime verifiedDate) {
        this.verifiedDate = verifiedDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void increaseAttemptCount() {
        this.attemptCount++;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresDate);
    }

    public boolean isVerified() {
        return verifiedDate != null;
    }

    public boolean hasExceededMaxAttempts(int maxAttempts) {
        return attemptCount >= maxAttempts;
    }
}
