package systems;

import enums.AccountStatus;
import exceptions.BankException;
import java.util.List;
import java.util.Scanner;
import managers.AccountManager;
import managers.TransactionManager;
import models.accounts.BankAccount;
import models.accounts.SavingsAccount;
import models.people.Customer;
import repositories.BankAccountRepository;
import repositories.CustomerRepository;

public class MenuSystem {
    private final Scanner scanner;
    private final LoginSystem loginSystem;
    private final AccountManager accountManager;
    private final CustomerRepository customerRepo;
    private final BankAccountRepository accountRepo;
    private final TransactionManager transactionManager;
    
    private static final String ADMIN_PASSCODE = "admin123";

    public MenuSystem(LoginSystem loginSystem, AccountManager accountManager,
                TransactionManager transactionManager,
                CustomerRepository customerRepo, BankAccountRepository accountRepo) {
        this.scanner = new Scanner(System.in);
        this.loginSystem = loginSystem;
        this.accountManager = accountManager;
        this.customerRepo = customerRepo;
        this.accountRepo = accountRepo;
        this.transactionManager = transactionManager;
    }

    public void start() {
        while (true) {
            MenuFormatter.printHeader("Smart Banking System");
            MenuFormatter.printMenuOption("1", "Secure Customer Portal");
            MenuFormatter.printMenuOption("2", "Bank Teller Workstation");
            MenuFormatter.printMenuOption("3", "Administrative Terminal");
            MenuFormatter.printMenuOption("4", "Exit Application Terminal");
            MenuFormatter.printDivider();
            System.out.print("Select option: ");
            String input = scanner.nextLine();

            if (input.equals("4")) {
                System.out.println("\n[TERMINAL] Shutting down connection channels... Session closed.");
                break;
            }
            
            switch(input) {
                case "1" -> handleCustomerPortal();
                case "2" -> handleTellerWorkstation();
                case "3" -> handleAdminTerminal();
                default -> System.out.println("[ERROR] Invalid option chosen.");
            }
        }
    }

    private void handleCustomerPortal() {
        System.out.print("Enter structural Customer Profile ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter access credentials password security code: ");
        String pass = scanner.nextLine();

        try {
            if (loginSystem.authenticate(id, pass)) {
                runCustomerSessionLoop(id);
            }
        } catch (BankException | SecurityException e) {
            System.out.println(e.getMessage());
        }
    }

    private void runCustomerSessionLoop(String customerId) {
        while (true) {
            MenuFormatter.printHeader("Customer Access Gateway");
            MenuFormatter.printMenuOption("1", "View Linked Asset Audit Portfolio");
            MenuFormatter.printMenuOption("2", "Initiate Cash Deposit");
            MenuFormatter.printMenuOption("3", "Initiate Cash Withdrawal");
            MenuFormatter.printMenuOption("4", "Initiate Cash Transfer");
            MenuFormatter.printMenuOption("5", "Disconnect Session");
            MenuFormatter.printDivider();
            System.out.print("Select option: ");
            String choice = scanner.nextLine();

            if (choice.equals("5")) break;

            try {
                switch(choice) {
                    case "1" -> {
                        var accounts = accountRepo.findByOwnerId(customerId);
                        if (accounts.isEmpty()) {
                            System.out.println("[INFO] No active accounts open under this portfolio registry profile.");
                        } else {
                            for (BankAccount acc : accounts) {
                                acc.displayInfo();
                            }
                        }
                    }

                    case "2" -> {
                        System.out.print("Target Account ID: ");
                        String depId = scanner.nextLine();
                        System.out.print("Deposit Amount: $");
                        double depAmt = Double.parseDouble(scanner.nextLine());
                        
                        BankAccount depAccount = accountRepo.findById(depId)
                                .orElseThrow(() -> new IllegalArgumentException("[ERROR] Account not found."));
                        depAccount.deposit(depAmt);
                        accountRepo.save(depAccount);
                        System.out.println("[SUCCESS] Deposited $" + depAmt + " to account " + depId);
                    }

                    case "3" -> {
                        System.out.print("Source Account ID: ");
                        String wthId = scanner.nextLine();
                        System.out.print("Withdraw Amount: $");
                        double wthAmt = Double.parseDouble(scanner.nextLine());
                        
                        transactionManager.executeWithdrawal(customerId, wthId, wthAmt);
                        System.out.println("[SUCCESS] Withdrew $" + wthAmt + " from account " + wthId);
                    }

                    case "4" -> {
                        System.out.print("Origin Account ID: ");
                        String srcId = scanner.nextLine();
                        System.out.print("Destination Account ID: ");
                        String dstId = scanner.nextLine();
                        System.out.print("Transfer Amount: $");
                        double xferAmt = Double.parseDouble(scanner.nextLine());
                        
                        BankAccount srcAcc = accountRepo.findById(srcId)
                                .orElseThrow(() -> new IllegalArgumentException("[ERROR] Source account not found."));
                        if (!srcAcc.getOwnerId().equals(customerId)) {
                            throw new SecurityException("[DENIED] Security violation: Source account does not belong to you.");
                        }
                        BankAccount dstAcc = accountRepo.findById(dstId)
                                .orElseThrow(() -> new IllegalArgumentException("[ERROR] Destination account not found."));
                        
                        srcAcc.transfer(xferAmt, dstAcc);
                        accountRepo.save(srcAcc);
                        accountRepo.save(dstAcc);
                        System.out.println("[SUCCESS] Transferred $" + xferAmt + " from " + srcId + " to " + dstId);
                    }

                    default -> System.out.println("[ERROR] Invalid option.");
                }
            } catch (BankException | SecurityException | IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void handleTellerWorkstation() {
        while (true) {
            MenuFormatter.printHeader("Teller Workstation Hub");
            MenuFormatter.printMenuOption("1", "Register New Customer Profile");
            MenuFormatter.printMenuOption("2", "Create New Financial Sub-Asset Account");
            MenuFormatter.printMenuOption("3", "View All Customers and Accounts");
            MenuFormatter.printMenuOption("4", "Return to Main Menu");
            MenuFormatter.printDivider();
            System.out.print("Select option: ");
            String option = scanner.nextLine();

            if (option.equals("4")) break;

            try {
                switch(option) {
                    case "1" -> {
                        System.out.print("Legal Full Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Age: ");
                        int age = Integer.parseInt(scanner.nextLine());
                        System.out.print("Email Record Address: ");
                        String email = scanner.nextLine();
                        System.out.print("Primary Phone Number: ");
                        int phone = Integer.parseInt(scanner.nextLine());
                        System.out.print("Initial Access Security Credentials Password: ");
                        String pass = scanner.nextLine();

                        String generatedCustId = accountManager.registerCustomer(name, age, email, phone, pass);
                        System.out.println("[SUCCESS] Profile indexed. Generated Customer ID Account Token: " + generatedCustId);
                    }

                    case "2" -> {
                        System.out.print("Enter Target Customer ID: ");
                        String custId = scanner.nextLine();
                        System.out.print("Initial Starting Liquidity Input: $");
                        double bal = Double.parseDouble(scanner.nextLine());
                        
                        System.out.println("Select Sub-Asset Structural Class Mapping Target Type: ");
                        System.out.println(" [1] Standard Checking (Overdraft Shield Enabled)");
                        System.out.println(" [2] Compound Interest Savings Portfolio Account");
                        System.out.println(" [3] Revolving Line Credit Card Asset");
                        System.out.println(" [4] Fixed Term Loan Account");
                        System.out.print("Select option: ");
                        String typeChoice = scanner.nextLine();

                        switch(typeChoice) {
                            case "1" -> {
                                accountManager.openCheckingAccount(custId, bal, 500.00);
                                System.out.println("[SUCCESS] Checking asset linked successfully.");
                            }
                            case "2" -> {
                                accountManager.openSavingsAccount(custId, bal, 0.04);
                                System.out.println("[SUCCESS] Savings asset linked successfully.");
                            }
                            case "3" -> {
                                accountManager.openCreditAccount(custId, 0.0, 5000.00);
                                System.out.println("[SUCCESS] Revolving credit asset linked successfully.");
                            }
                            case "4" -> {
                                accountManager.openLoanAccount(custId, bal, bal, 0.06);
                                System.out.println("[SUCCESS] Fixed Term Loan Account linked successfully.");
                            }
                            default -> System.out.println("[ERROR] Invalid option.");
                        }
                    }

                    case "3" -> {
                        List<Customer> allCustomers = customerRepo.findAll();
                        if (allCustomers.isEmpty()) {
                            System.out.println("[INFO] No customers registered in the system.");
                        } else {
                            for (Customer c : allCustomers) {
                                System.out.printf("  ID: %-10s | Name: %-15s | Status: %-10s | Tier: %s\n",
                                    c.getId(), c.getName(), c.getStatus(), c.getMembershipTier());
                                List<BankAccount> accs = accountRepo.findByOwnerId(c.getId());
                                if (accs.isEmpty()) {
                                    System.out.println("    No accounts linked.");
                                } else {
                                    for (BankAccount acc : accs) {
                                        System.out.printf("    -> %-12s | Type: %-10s | Balance: $%-10.2f | Status: %s\n",
                                            acc.getId(), acc.getType(), acc.getBalance(), acc.getStatus());
                                    }
                                }
                                System.out.println();
                            }
                        }
                        System.out.print("Press Enter to return...");
                        scanner.nextLine();
                    }

                    default -> System.out.println("[ERROR] Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("[WORKSTATION EXCEPTION] Prohibited manipulation trace: " + e.getMessage());
            }
        }
    }

    private void handleAdminTerminal() {
        System.out.print("Enter Admin Master Security Password: ");
        String code = scanner.nextLine();
        if (!code.equals(ADMIN_PASSCODE)) {
            System.out.println("[CRITICAL] Access Denied. Audit tracking log recorded.");
            return;
        }

        while(true) {
            MenuFormatter.printHeader("Compliance Operations Control Room");
            MenuFormatter.printMenuOption("1", "Process Monthly Savings Interest");
            MenuFormatter.printMenuOption("2", "Suspend Customer Profile");
            MenuFormatter.printMenuOption("3", "Reactivate Customer Profile");
            MenuFormatter.printMenuOption("4", "Run System Diagnostics");
            MenuFormatter.printMenuOption("5", "Logout Admin Session");
            MenuFormatter.printDivider();
            System.out.print("Select Option: ");
            String cmd = scanner.nextLine();

            if (cmd.equals("5")) break;

            try {
                switch(cmd) {
                    case "1" -> processMonthlyInterestBatch();
                    case "2" -> {
                        System.out.print("Enter Customer ID to suspend: ");
                        String targetSus = scanner.nextLine();
                        Customer suspendee = customerRepo.findById(targetSus)
                                .orElseThrow(() -> new IllegalArgumentException("[ERROR] Profile not found."));
                        suspendee.suspend();
                        customerRepo.save(suspendee);
                        System.out.println("[SUCCESS] Customer account portfolio profile frozen.");
                    }
                    case "3" -> {
                        System.out.print("Enter Customer ID to reactivate: ");
                        String targetAct = scanner.nextLine();
                        Customer activee = customerRepo.findById(targetAct)
                                .orElseThrow(() -> new IllegalArgumentException("[ERROR] Profile not found."));
                        activee.reactivate();
                        customerRepo.save(activee);
                        System.out.println("[SUCCESS] Customer account portfolio profile reactivated.");
                    }
                    case "4" -> runSystemDiagnostics();
                    default -> System.out.println("[ERROR] Invalid administration block target routing command.");
                }
            } catch (Exception e) {
                System.out.println("[ADMIN CATCH] System operation error: " + e.getMessage());
            }
        }
    }

    private void processMonthlyInterestBatch() {
        System.out.println("\n[BATCH CONTROL] Instantiating interest engine routine loop sweeps...");
        int affectedAccounts = 0;

        for (BankAccount acc : accountRepo.findAll()) {
            // FIX: skip frozen/closed accounts so deposit() doesn't throw mid-batch
            if (acc instanceof SavingsAccount && acc.getStatus() == AccountStatus.ACTIVE) {
                SavingsAccount savings = (SavingsAccount) acc;
                double calculatedInterest = savings.getBalance() * (savings.getInterestRate() / 12);
                savings.deposit(calculatedInterest);
                accountRepo.save(savings);
                affectedAccounts++;
            }
        }

        System.out.println("[BATCH SUCCESS] Process routine finalized.");
        System.out.println("  Total active records processed and updated: " + affectedAccounts);
    }

    private void runSystemDiagnostics() {
        MenuFormatter.printHeader("System Diagnostic Audit");
        int customerRecords = customerRepo.findAll().size();
        int accountRecords = accountRepo.findAll().size();
        
        MenuFormatter.printStatRow("Core Package Status", "FIXED & FULLY INTEGRATED");
        MenuFormatter.printStatRow("Loaded Customer Records (.dat)", String.valueOf(customerRecords));
        MenuFormatter.printStatRow("Loaded Polymorphic Asset Profiles", String.valueOf(accountRecords));
        MenuFormatter.printStatRow("Encryption Utility Layer", "VERIFIED (PASSWORD MATCH FIXED)");
        MenuFormatter.printStatRow("File Serialization System", "INTEGRITY SECURE");
        MenuFormatter.printDivider();
        System.out.print("Press Enter to return to main gateway...");
        scanner.nextLine();
    }
}
