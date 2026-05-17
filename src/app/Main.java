package app;

import managers.AccountManager;
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
            // 1. Initialize Persistence Repositories (e.g., File-based or Memory-based)
            // Ensure these implement CustomerRepository / BankAccountRepository respectively
            CustomerRepository customerRepo = new CustomerRepository();
            BankAccountRepository accountRepo = new BankAccountRepository();

            System.out.println("[CORE] Persistence systems booted successfully.");

            // 2. Initialize Core Management Component
            AccountManager accountManager = new AccountManager(accountRepo, customerRepo);
            System.out.println("[CORE] Operational Business Manager initialized.");

            // 3. Initialize Security & Authentication Gateway
            LoginSystem loginSystem = new LoginSystem(customerRepo);
            System.out.println("[CORE] Security Gateways and RBAC logs linked.");

            // 4. Instantiate the Consolidated Menu Core Engine
            MenuSystem menuSystem = new MenuSystem(loginSystem, accountManager, customerRepo, accountRepo);
            System.out.println("[CORE] UI Control Console attached.");
            System.out.println("=================================================\n");

            // 5. Transfer Execution Control Over to User Interface Flow
            menuSystem.start();

        } catch (Exception e) {
            System.err.println("\n[CRITICAL FAILURE] Core system crashed during initialization sequence!");
            e.printStackTrace();
        }
    }
}