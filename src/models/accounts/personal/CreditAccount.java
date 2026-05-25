package models.accounts.personal;

import models.accounts.BankAccount;
import enums.PersonalType;
import exceptions.InsufficientFundsException;

public class CreditAccount extends BankAccount {
    private static final long serialVersionUID = 1L;
    
    private final double creditLimit;

    public CreditAccount(String ownerId, double startingBalance, double creditLimit) {
        super(ownerId, startingBalance); // Starting balance here acts as initial outstanding debt balance
        this.creditLimit = creditLimit;
    }

    @Override
    public PersonalType getType() {
        return PersonalType.CREDIT;
    }

    public double getCreditLimit() {
        return creditLimit;
    }
    
    public double getAvailableCredit() {
        return creditLimit - getBalance();
    }

    @Override
    public void withdraw(double amount) {
        validateAccountActive();
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        if (amount > getAvailableCredit()) {
            throw new InsufficientFundsException("[ERROR] Transaction denied: Exceeds assigned Credit Card limit.");
        }
        setBalance(getBalance() + amount); // Debt increases on withdrawal
        recordTransaction("Credit card purchase: +" + String.format("$%.2f", amount));
    }

    @Override
    public void deposit(double amount) {
        validateAccountActive();
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        setBalance(getBalance() - amount); // Debt decreases on payments
        recordTransaction("Credit card statement payment received: -" + String.format("$%.2f", amount));
    }
}
