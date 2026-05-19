package managers;

import exceptions.CustomerNotFoundException;
import interfaces.CrudRepository;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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

    public String registerCustomer(String name, int age, String email, int phone, String password) {
        String generatedId = "CUST-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Customer newCustomer = new Customer(generatedId, name, age, email, phone, password);
        customerRepo.save(newCustomer);
        return generatedId;
    }

    public Optional<BankAccount> findAccount(String accountId) {
        return accountRepo.findById(accountId);
    }

    public void openCheckingAccount(String customerId, double startingBalance, double overdraftLimit) {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Profile reference missing for: " + customerId));
        
        CheckingAccount account = new CheckingAccount(customerId, startingBalance, overdraftLimit);
        accountRepo.save(account);
        
        // Save customer changes securely without trapping stale account states
        customerRepo.save(customer);
    }

    public void openSavingsAccount(String customerId, double startingBalance, double interestRate) {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Profile reference missing for: " + customerId));
            
        SavingsAccount account = new SavingsAccount(customerId, startingBalance, interestRate);
        accountRepo.save(account);
        
        customerRepo.save(customer);
    }

    public void openCreditAccount(String customerId, double startingBalance, double creditLimit) {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Profile reference missing for: " + customerId));
            
        CreditAccount account = new CreditAccount(customerId, startingBalance, creditLimit);
        accountRepo.save(account);
        
        customerRepo.save(customer);
    }

    public void openLoanAccount(String customerId, double startingBalance, double initialPrincipal, double interestRate) {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Profile reference missing for: " + customerId));
            
        LoanAccount account = new LoanAccount(customerId, startingBalance, initialPrincipal, interestRate);
        accountRepo.save(account);
        
        customerRepo.save(customer);
    }

    /**
     * Dynamically queries data models directly from repo storage.
     * Prevents data sync errors across files.
     */
    public List<BankAccount> getAllAccountsForCustomer(String customerId) {
        List<BankAccount> matched = new ArrayList<>();
        for (BankAccount acc : accountRepo.findAll()) {
            if (acc.getOwnerId().equals(customerId)) {
                matched.add(acc);
            }
        }
        return matched;
    }

    public void closeAccount(String accountId) {
        BankAccount account = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Account not found: " + accountId));

        account.close(); 
        accountRepo.save(account); 
        System.out.println("[SUCCESS] Account " + accountId + " has been officially CLOSED.");
    }

    public void freezeAccount(String accountId) {
        BankAccount account = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Account not found: " + accountId));

        account.freeze(); 
        accountRepo.save(account); 
        System.out.println("[SUCCESS] Account " + accountId + " has been FROZEN by administrative override.");
    }

    public void reactivateAccount(String accountId) {
        BankAccount account = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Account not found: " + accountId));

        account.reactivate(); 
        accountRepo.save(account); 
        System.out.println("[SUCCESS] Account " + accountId + " is now ACTIVE.");
    }
}