package com.williamhsieh.financeapp.entity.institution;

import java.time.LocalDateTime;

import com.williamhsieh.financeapp.entity.region.Country;

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
    name = "financial_institutions",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_financial_institutions_country_code",
            columnNames = {
                "country_id",
                "code"
            }
        )
    }
)
public class FinancialInstitution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "financial_institution_type_id",
        nullable = false
    )
    private FinancialInstitutionType financialInstitutionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_institution_group_id")
    private FinancialInstitutionGroup financialInstitutionGroup;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "legal_name", nullable = false, length = 150)
    private String legalName;

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

    protected FinancialInstitution() {
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

    public FinancialInstitutionType getFinancialInstitutionType() {
        return financialInstitutionType;
    }

    public void setFinancialInstitutionType(
        FinancialInstitutionType financialInstitutionType
    ) {
        this.financialInstitutionType =
            financialInstitutionType;
    }

    public FinancialInstitutionGroup getFinancialInstitutionGroup() {
        return financialInstitutionGroup;
    }

    public void setFinancialInstitutionGroup(
        FinancialInstitutionGroup financialInstitutionGroup
    ) {
        this.financialInstitutionGroup =
            financialInstitutionGroup;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
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
