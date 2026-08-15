package com.williamhsieh.financeapp.entity.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.williamhsieh.financeapp.entity.region.Country;
import com.williamhsieh.financeapp.entity.region.Language;
import com.williamhsieh.financeapp.entity.region.Timezone;

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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timezone_id")
    private Timezone timezone;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @Column(
        name = "user_number",
        nullable = false,
        unique = true,
        length = 10
    )
    private String userNumber;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;

    @Column(
        name = "email",
        nullable = false,
        unique = true,
        length = 255
    )
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "sms_verified", nullable = false)
    private boolean smsVerified = false;

    @Column(name = "is_active", nullable = false)
    private boolean active = false;

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

    public User() {
    }

    public Long getId() {
        return id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Timezone getTimezone() {
        return timezone;
    }

    public void setTimezone(Timezone timezone) {
        this.timezone = timezone;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isSmsVerified() {
        return smsVerified;
    }

    public void setSmsVerified(boolean smsVerified) {
        this.smsVerified = smsVerified;
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

    public void refreshActiveStatus() {
        this.active = this.emailVerified && this.smsVerified;
    }
}
