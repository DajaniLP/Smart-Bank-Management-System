package models.accounts.personal;

import enums.PersonalType;
import models.accounts.BankAccount;

public class LoanAccount extends BankAccount {
    private static final long serialVersionUID = 1L;

    private final double initialPrincipal;
    private final double interestRate;

    public LoanAccount(String ownerId, double startingBalance, double initialPrincipal, double interestRate) {
        super(ownerId, startingBalance); // Current balance acts as current outstanding debt payoff amount
        this.initialPrincipal = initialPrincipal;
        this.interestRate = interestRate;
    }

    @Override
    public PersonalType getType() {
        return PersonalType.LOAN;
    }

    public double getInitialPrincipal() {
        return initialPrincipal;
    }

    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public void withdraw(double amount) {
        // Block manual liquidation withdrawals from a loan asset entirely
        throw new UnsupportedOperationException("[DENIED] Operations Error: Manual cash liquidations are prohibited from active Loan accounts.");
    }

    @Override
    public void deposit(double amount) {
        validateAccountActive();
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment allocation amount must be positive.");
        }
        
        // Paying down the outstanding balance debt
        setBalance(getBalance() - amount);
        recordTransaction("Loan principal payment processed: -" + String.format("$%.2f", amount));
        
        System.out.println("-------------------------------------------------");
        System.out.println("» LOAN PAYMENT PROCESSED SUCCESSFULLY");
        System.out.println("  Amount Allocated: -$" + String.format("%.2f", amount));
        System.out.println("  Remaining Balance: $" + String.format("%.2f", getBalance()));
        System.out.println("-------------------------------------------------");
    }
}