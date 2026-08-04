package com.williamhsieh.financeapp.entity.account;

import com.williamhsieh.financeapp.entity.institution.FinancialInstitution;
import com.williamhsieh.financeapp.entity.schedule.FlowFrequency;

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
@Table(name = "bank_accounts")
public class BankAccount {
    
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
    @JoinColumn(name = "bank_account_type_id")
    private BankAccountType bankAccountType;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "is_digital", nullable = false)
    private boolean digital = false;

    @Column(name = "has_passbook", nullable = false)
    private boolean hasPassbook = false;

    @Column(name = "statement_day")
    private Byte statementDay;

    @Column(name = "settlement_days")
    private Integer settlementDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payout_frequency_id")
    private FlowFrequency payoutFrequency;

    protected BankAccount() {
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

    public BankAccountType getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(
        BankAccountType bankAccountType
    ) {
        this.bankAccountType = bankAccountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean isDigital() {
        return digital;
    }

    public void setDigital(boolean digital) {
        this.digital = digital;
    }

    public boolean hasPassbook() {
        return hasPassbook;
    }

    public void setHasPassbook(boolean hasPassbook) {
        this.hasPassbook = hasPassbook;
    }

    public Byte getStatementDay() {
        return statementDay;
    }

    public void setStatementDay(Byte statementDay) {
        this.statementDay = statementDay;
    }

    public Integer getSettlementDays() {
        return settlementDays;
    }

    public void setSettlementDays(Integer settlementDays) {
        this.settlementDays = settlementDays;
    }

    public FlowFrequency getPayoutFrequency() {
        return payoutFrequency;
    }

    public void setPayoutFrequency(
        FlowFrequency payoutFrequency
    ) {
        this.payoutFrequency = payoutFrequency;
    }
}
