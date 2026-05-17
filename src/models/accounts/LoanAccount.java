package models.accounts;

import enums.AccountType;

public class LoanAccount extends BankAccount {
    private static final long serialVersionUID = 1L;

    private final double initialPrincipal;
    private final double interestRate;

    public LoanAccount(String ownerId, double startingBalance, double initialPrincipal, double interestRate) {
        // The starting balance fields track outstanding principal debt liability
        super(ownerId);
        this.initialPrincipal = initialPrincipal;
        this.interestRate = interestRate;
    }

    @Override
    public AccountType getType() {
        return AccountType.LOAN;
    }

    @Override
    public void withdraw(double amount) {
        throw new UnsupportedOperationException("[DENIED] Operations Error: Manual withdrawals are prohibited from active Loan accounts.");
    }

    public double getInitialPrincipal() {
        return initialPrincipal;
    }

    public double getInterestRate() {
        return interestRate;
    }
}