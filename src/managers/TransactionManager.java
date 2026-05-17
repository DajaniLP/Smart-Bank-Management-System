package managers;

import interfaces.CrudRepository;
import java.io.Serializable;
import models.accounts.BankAccount;

public class TransactionManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CrudRepository<BankAccount, String> accountRepo;

    public TransactionManager(CrudRepository<BankAccount, String> accountRepo) {
        this.accountRepo = accountRepo;
    }

    // Process a deposit into an account
    public void executeDeposit(String accountId, double amount) {
        BankAccount account = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Account not found: " + accountId));

        account.deposit(amount);
        accountRepo.save(account);
        System.out.println("[SUCCESS] Deposited $" + amount + " to account " + accountId);
    }

    // Process a withdrawal from an account
    public void executeWithdrawal(String accountId, double amount) {
        BankAccount account = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Account not found: " + accountId));

        account.withdraw(amount);
        accountRepo.save(account);
        System.out.println("[SUCCESS] Withdrew $" + amount + " from account " + accountId);
    }

    // Process a transfer between two accounts
    public void executeTransfer(String sourceAccountId, String targetAccountId, double amount) {
        BankAccount sourceAccount = accountRepo.findById(sourceAccountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Source account not found: " + sourceAccountId));
            
        BankAccount targetAccount = accountRepo.findById(targetAccountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Target account not found: " + targetAccountId));

        sourceAccount.transfer(amount, targetAccount);

        accountRepo.save(sourceAccount);
        accountRepo.save(targetAccount);
        System.out.println("[SUCCESS] Transferred $" + amount + " from " + sourceAccountId + " to " + targetAccountId);
    }
}