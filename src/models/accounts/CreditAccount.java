package models.accounts;

import enums.AccountType;

public class CreditAccount extends BankAccount {
    private static final long serialVersionUID = 1L;
    
    private final double creditLimit;

    public CreditAccount(String ownerId, double startingBalance, double creditLimit) {
        // Revolving credit cards start with $0.0 outstanding balance debt
        super(ownerId, startingBalance);
        this.creditLimit = creditLimit;
    }

    @Override
    public AccountType getType() {
        return AccountType.CREDIT;
    }

    public double getCreditLimit() {
        return creditLimit;
    }
    
    public double getAvailableCredit() {
        return creditLimit - getBalance();
    }
}
