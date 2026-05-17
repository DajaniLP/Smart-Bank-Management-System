package models.accounts;

import enums.AccountType;

public class SavingsAccount extends BankAccount {
    private static final long serialVersionUID = 1L;

    // e.g., 0.04 for 4%
    private double interestRate;

    public SavingsAccount(String ownerId, double startingBalance, double interestRate) {
        super(ownerId, startingBalance);
        this.interestRate = interestRate;
    }

    @Override 
    public AccountType getType() {
        return AccountType.SAVINGS;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
}