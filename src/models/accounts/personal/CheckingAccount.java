package models.accounts.personal;

import enums.PersonalType;
import exceptions.InsufficientFundsException;
import models.accounts.BankAccount;

public class CheckingAccount extends BankAccount {
    private static final long serialVersionUID = 1L;

    private final double overdraftLimit;

    public CheckingAccount(String ownerId, double startingBalance, double overdraftLimit) {
        super(ownerId, startingBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public PersonalType getType() {
        return PersonalType.CHECKING;
    }
    
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public void withdraw(double amount) {
        validateAccountActive();
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        
        // Overdraft rule check: balance + overdraft threshold protection limit
        if (amount > (getBalance() + overdraftLimit)) {
            throw new InsufficientFundsException("[ERROR] Transaction exceeds authorized checking overdraft limit.");
        }
        
        setBalance(getBalance() - amount);
        recordTransaction("Withdrew (Checking/Overdraft): $" + String.format("%.2f", amount));
        
        System.out.println("-------------------------------------------------");
        System.out.println("» WITHDRAWAL SUCCESSFUL (CHECKING)");
        System.out.println("  Amount Deducted: -$" + String.format("%.2f", amount));
        System.out.println("  New Balance:     $" + String.format("%.2f", getBalance()));
        System.out.println("-------------------------------------------------");
    }
}