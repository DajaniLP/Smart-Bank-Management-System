package interfaces;

import models.accounts.BankAccount;

public interface TransactionAction {
    void deposit(double amount);
    void withdraw(double amount);
    void transfer(double amount, BankAccount targetAccount);
}