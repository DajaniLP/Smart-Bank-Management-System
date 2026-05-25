package app;

import managers.AccountManager;
import managers.CustomerManager;
import managers.TransactionManager;
import repositories.BankAccountRepository;
import repositories.CustomerRepository;
import systems.LoginSystem;
import systems.MenuSystem;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("||       INITIALIZING SMART BANKING CORE       ||");
        System.out.println("=================================================");

        try {
            CustomerRepository customerRepo = new CustomerRepository();
            BankAccountRepository accountRepo = new BankAccountRepository();
            System.out.println("[CORE] Persistence systems booted successfully.");

            AccountManager accountManager = new AccountManager(accountRepo, customerRepo);
            CustomerManager customerManager = new CustomerManager(customerRepo);
            TransactionManager transactionManager = new TransactionManager(accountRepo, customerRepo);
            System.out.println("[CORE] Operational Business, Customer, and Transaction Managers initialized.");

            LoginSystem loginSystem = new LoginSystem(customerRepo);
            System.out.println("[CORE] Security Gateways and RBAC logs linked.");

            MenuSystem menuSystem = new MenuSystem(loginSystem, accountManager, transactionManager, customerRepo, accountRepo);            
            System.out.println("[CORE] UI Control Console attached.");
            System.out.println("=================================================\n");

            menuSystem.start();

        } catch (Exception e) {
            System.err.println("\n[CRITICAL FAILURE] Core system crashed during initialization sequence!");
        }
    }
}