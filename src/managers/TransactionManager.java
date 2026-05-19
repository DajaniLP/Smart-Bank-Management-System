package managers;

import exceptions.CustomerNotFoundException;
import exceptions.InsufficientFundsException;
import interfaces.CrudRepository;
import interfaces.TransactionAction; // 1. Import your interface explicitly
import java.io.Serializable;
import models.accounts.BankAccount;
import models.people.Customer;

public class TransactionManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CrudRepository<BankAccount, String> accountRepo;
    private final CrudRepository<Customer, String> customerRepo;

    public TransactionManager(CrudRepository<BankAccount, String> accountRepo, CrudRepository<Customer, String> customerRepo) {
        this.accountRepo = accountRepo;
        this.customerRepo = customerRepo;
    }

    public void executeDeposit(String accountId, double amount) {
        BankAccount account = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Target account not found: " + accountId));

        TransactionAction actionTarget = account; 
        actionTarget.deposit(amount); 
        
        accountRepo.save(account);
        System.out.println("[SUCCESS] Deposited $" + amount + " to account " + accountId);
    }

    public void executeWithdrawal(String customerId, String accountId, double amount) {
        BankAccount account = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Target account not found: " + accountId));

        if (!account.getOwnerId().equals(customerId)) {
            throw new SecurityException("[DENIED] Security violation: Unauthorized asset access attempt.");
        }

        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Customer profile missing."));

        if (amount > customer.getMembershipTier().getWithdrawalLimit()) {
            throw new InsufficientFundsException("[DENIED] Transaction aborted: Amount breaches limits.");
        }

        TransactionAction actionTarget = account;
        actionTarget.withdraw(amount);

        accountRepo.save(account);
        System.out.println("[SUCCESS] Withdrew $" + amount + " from account " + accountId);
    }

    public void executeTransfer(String customerId, String sourceAccountId, String targetAccountId, double amount) {
        BankAccount sourceAccount = accountRepo.findById(sourceAccountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Source account not found."));
            
        BankAccount targetAccount = accountRepo.findById(targetAccountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Target destination account not found."));

        if (!sourceAccount.getOwnerId().equals(customerId)) {
            throw new SecurityException("[DENIED] Security violation: Access denied.");
        }

        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Customer profile missing."));

        if (amount > customer.getMembershipTier().getTransferLimit()) {
            throw new InsufficientFundsException("[DENIED] Transaction aborted: Amount breaches limits.");
        }

        // 4. Clean interface polymorphic execution
        TransactionAction sourceAction = sourceAccount;
        sourceAction.transfer(amount, targetAccount);

        accountRepo.save(sourceAccount);
        accountRepo.save(targetAccount);
        System.out.println("[SUCCESS] Transferred $" + amount + " from " + sourceAccountId + " to " + targetAccountId);
    }
}