package managers;

import exceptions.CustomerNotFoundException;
import interfaces.CrudRepository;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;
import models.accounts.*;
import models.people.Customer;

public class AccountManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CrudRepository<BankAccount, String> accountRepo;
    private final CrudRepository<Customer, String> customerRepo;

    public AccountManager(CrudRepository<BankAccount, String> accountRepo, CrudRepository<Customer, String> customerRepo) {
        this.accountRepo = accountRepo;
        this.customerRepo = customerRepo;
    }

    // Provision new customer profile (Required by MenuSystem choice 1)
    public String registerCustomer(String name, int age, String email, int phone, String password) {
        String generatedId = "CUST-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Customer newCustomer = new Customer(generatedId, name, age, email, phone, password);
        customerRepo.save(newCustomer);
        return generatedId;
    }

    // Lookup an account by ID
    public Optional<BankAccount> findAccount(String accountId) {
        return accountRepo.findById(accountId);
    }

    // Provision checking account
    public void openCheckingAccount(String customerId, double startingBalance, double overdraftLimit) {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Customer profile not found: " + customerId));

        CheckingAccount account = new CheckingAccount(customerId, startingBalance, overdraftLimit);
        customer.addAccount(account);

        accountRepo.save(account);
        customerRepo.save(customer);
        System.out.println("[SUCCESS] Checking Account " + account.getId() + " opened for [" + customerId + "]");
    }

    // Provision credit account
    public void openCreditAccount(String customerId, double startingBalance, double creditLimit) {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Customer profile not found: " + customerId));

        CreditAccount account = new CreditAccount(customerId, startingBalance, creditLimit);
        customer.addAccount(account);

        accountRepo.save(account);
        customerRepo.save(customer);
        System.out.println("[SUCCESS] Credit Account " + account.getId() + " opened for [" + customerId + "]");
    }

    // Provision loan account (Fixed to 3 parameters to match MenuSystem and LoanAccount constructor)
    public void openLoanAccount(String customerId, double startingBalance, double initialPrincipal, double interestRate) {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Customer profile not found: " + customerId));

        LoanAccount account = new LoanAccount(customerId, startingBalance, initialPrincipal, interestRate);
        customer.addAccount(account);

        accountRepo.save(account);
        customerRepo.save(customer);
        System.out.println("[SUCCESS] Loan Account " + account.getId() + " opened for [" + customerId + "]");
    }

    // Provision savings account
    public void openSavingsAccount(String customerId, double startingBalance, double interestRate) {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Customer profile not found: " + customerId));

        SavingsAccount account = new SavingsAccount(customerId, startingBalance, interestRate);
        customer.addAccount(account);

        accountRepo.save(account);
        customerRepo.save(customer);
        System.out.println("[SUCCESS] Savings Account " + account.getId() + " opened for [" + customerId + "]");
    }

    // Execute safe, verified withdrawal transaction rules (Required by MenuSystem customer portal)
    public void executeManagedWithdrawal(String accountId, String customerId, double amount) {
        BankAccount account = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Execution halted. The specified account ID does not exist."));

        if (!account.getOwnerId().equals(customerId)) {
            throw new SecurityException("[DENIED] Security violation: This account does not belong to you.");
        }

        // Activates overridden polymorphism rule variations
        account.withdraw(amount);
        
        // Flushes updated fields immediately to persistence files
        accountRepo.save(account);
    }

    // Close account permanently
    public void closeAccount(String accountId) {
        BankAccount account = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Account not found: " + accountId));

        account.close(); 
        accountRepo.save(account); 
    }

    // Freeze account to lock activity
    public void freezeAccount(String accountId) {
        BankAccount account = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Account not found: " + accountId));

        account.freeze(); 
        accountRepo.save(account); 
    }

    // Reactivate a frozen account
    public void reactivateAccount(String accountId) {
        BankAccount account = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Account not found: " + accountId));

        account.reactivate(); 
        accountRepo.save(account); 
    }
}