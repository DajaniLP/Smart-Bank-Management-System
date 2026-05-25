package interfaces;

import models.accounts.BankAccount;

public interface TransactionAction {
    void deposit(double amount);
    void withdraw(double amount);
    void transfer(double amount, BankAccount targetAccount);
    static void executeWithdrawal(String customerId, String wthId, double wthAmt) {
        throw new UnsupportedOperationException("Unimplemented method 'executeWithdrawal'");
    }
}