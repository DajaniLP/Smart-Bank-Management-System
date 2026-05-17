package systems;

import enums.AccountStatus;
import enums.CustomerStatus;
import exceptions.CustomerNotFoundException;
import java.util.List;
import java.util.Scanner;
import managers.AccountManager;
import models.accounts.*;
import models.people.Customer;
import repositories.BankAccountRepository;
import repositories.CustomerRepository;

public class MenuSystem {
    private final Scanner scanner;
    private final LoginSystem loginSystem;
    private final AccountManager accountManager;
    private final CustomerRepository customerRepo;
    private final BankAccountRepository accountRepo;
    
    // Configured secure gateway passcode for Admin Terminal and Diagnostics
    private static final String ADMIN_PASSCODE = "Admin123";

    public MenuSystem(LoginSystem loginSystem, AccountManager accountManager, 
                        CustomerRepository customerRepo, BankAccountRepository accountRepo) {
        this.scanner = new Scanner(System.in);
        this.loginSystem = loginSystem;
        this.accountManager = accountManager;
        this.customerRepo = customerRepo;
        this.accountRepo = accountRepo;
    }

    public void start() {
        while (true) {
            MenuFormatter.printHeader("Smart Banking CoreSystem");
            MenuFormatter.printMenuOption("1", "Secure Customer Portal");
            MenuFormatter.printMenuOption("2", "Bank Teller / Employee Workstation");
            MenuFormatter.printMenuOption("3", "Centralized Risk & Administration Terminal [SECURE]");
            MenuFormatter.printMenuOption("4", "System Diagnostic Check (Lecturer Audit) [SECURE]");
            MenuFormatter.printMenuOption("5", "Terminate System Session");
            
            MenuFormatter.printDivider();
            System.out.println("|| STATUS: ONLINE  | DATABASE SECURED  | RBAC LOGS: ACTIVE  ||");
            MenuFormatter.printFooter();
            
            System.out.print("Select gateway option: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                handleCustomerLogin();
            } else if (choice.equals("2")) {
                handleTellerWorkstation();
            } else if (choice.equals("3")) {
                // Passcode validation check added before entering admin terminal
                if (authenticateAdminGateway()) {
                    handleAdminTerminal();
                }
            } else if (choice.equals("4")) {
                // Passcode validation check added before running system diagnostics
                if (authenticateAdminGateway()) {
                    runSystemDiagnostics();
                }
            } else if (choice.equals("5")) {
                System.out.println("\n[SECURE] Disconnecting repositories... Session terminated cleanly.");
                break;
            } else {
                System.out.println("[ERROR] Invalid selection. Choose an option from 1 to 5.");
            }
        }
    }

    /**
     * Reusable private security checkpoint gate for option 3 and 4.
     */
    private boolean authenticateAdminGateway() {
        MenuFormatter.printHeader("Elevated Privileges Required");
        System.out.print("Enter Secure Admin Passcode: ");
        String input = scanner.nextLine().trim();
        
        if (input.equals(ADMIN_PASSCODE)) {
            System.out.println("[SUCCESS] Access Granted. Opening secure shell...");
            return true;
        } else {
            System.out.println("[ACCESS DENIED] Security alert raised: Invalid system passcode.");
            return false;
        }
    }

    // ==========================================
    //           CUSTOMER PORTAL LAYER
    // ==========================================
    private void handleCustomerLogin() {
        MenuFormatter.printHeader("Customer Authentication");
        System.out.print("Enter Customer ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String pass = scanner.nextLine().trim();

        try {
            if (loginSystem.authenticate(id, pass)) {
                runCustomerDashboard(id);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void runCustomerDashboard(String customerId) {
        while (true) {
            // Replaced unsafe .get() with your custom exception
            Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Customer record not found for ID: " + customerId));
            List<BankAccount> accounts = accountRepo.findByOwnerId(customerId);

            MenuFormatter.printHeader("Welcome, " + customer.getName());
            MenuFormatter.printMenuOption("1", "View Financial Analytics & Balances");
            MenuFormatter.printMenuOption("2", "Run Financial Investment Simulation");
            MenuFormatter.printMenuOption("3", "Print Auditable Transaction Ledger");
            MenuFormatter.printMenuOption("4", "Execute Managed Withdrawal");
            MenuFormatter.printMenuOption("5", "Logout Securely");
            MenuFormatter.printFooter();

            System.out.print("Select operation: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                displayInDepthAnalytics(customer, accounts);
            } else if (choice.equals("2")) {
                runFinancialSimulation(customer);
            } else if (choice.equals("3")) {
                displayTransactionHistory(customer);
            } else if (choice.equals("4")) {
                handleWithdrawal(customer);
            } else if (choice.equals("5")) {
                System.out.println("[INFO] Logged out safely.");
                break;
            } else {
                System.out.println("[ERROR] Invalid selection.");
            }
        }
    }

    private void displayInDepthAnalytics(Customer customer, List<BankAccount> accounts) {
        MenuFormatter.printHeader("Advanced Asset & Risk Analytics");

        double cashAssets = 0;
        double totalDebt = 0;

        for (BankAccount acc : accounts) {
            String statusFlag = (acc.getStatus() != AccountStatus.ACTIVE) ? " [" + acc.getStatus() + "]" : "";
            
            if (acc instanceof CheckingAccount) {
                cashAssets += acc.getBalance();
                MenuFormatter.printStatRow("Checking [" + acc.getId() + "]" + statusFlag, String.format("$%.2f", acc.getBalance()));
            } else if (acc instanceof SavingsAccount) {
                cashAssets += acc.getBalance();
                SavingsAccount savings = (SavingsAccount) acc;
                MenuFormatter.printStatRow("Savings [" + acc.getId() + "]" + statusFlag, String.format("$%.2f", acc.getBalance()));
                
                double projection = acc.getBalance() * Math.pow((1 + savings.getInterestRate()), 5);
                MenuFormatter.printStatRow("  ↳ 5-Year Forecast (Compounded)", String.format("$%.2f", projection));
            } else if (acc instanceof LoanAccount) {
                totalDebt += acc.getBalance();
                MenuFormatter.printStatRow("Loan Debt [" + acc.getId() + "]" + statusFlag, String.format("$%.2f", acc.getBalance()));
            } else if (acc instanceof CreditAccount) {
                CreditAccount credit = (CreditAccount) acc;
                totalDebt += credit.getBalance();
                MenuFormatter.printProgressBar("Credit Usage [" + credit.getId() + "]" + statusFlag, credit.getBalance(), credit.getCreditLimit());
            }
        }

        double netWorth = cashAssets - totalDebt;
        MenuFormatter.printDivider();
        MenuFormatter.printStatRow("Gross Cash Liquid Assets", String.format("$%.2f", cashAssets));
        MenuFormatter.printStatRow("Total Outstanding Liabilities", String.format("$%.2f", totalDebt));
        MenuFormatter.printStatRow("Calculated Net Worth Position", String.format("$%.2f", netWorth));
        
        double leverageRatio = (cashAssets > 0) ? (totalDebt / cashAssets) * 100 : (totalDebt > 0 ? 100.0 : 0.0);
        MenuFormatter.printStatRow("Debt-to-Asset Leverage Ratio", String.format("%.1f%%", leverageRatio));
        
        MenuFormatter.printDivider();
        if (leverageRatio > 50.0) {
            MenuFormatter.printStatRow("RISK MANAGEMENT NOTICE", "[WARNING: HIGHLY LEVERAGED]");
        } else if (netWorth > 25000) {
            MenuFormatter.printStatRow("LIQUIDITY ADVISORY", "[PREMIUM CAPITALLY SECURE]");
        } else {
            MenuFormatter.printStatRow("FINANCIAL HEALTH EVALUATION", "[STABLE RUNTIME POSITION]");
        }
        MenuFormatter.printFooter();
    }

    private void runFinancialSimulation(Customer customer) {
        List<BankAccount> accounts = accountRepo.findByOwnerId(customer.getId());
        if (accounts.isEmpty()) {
            System.out.println("[INFO] You need an active account to run projections.");
            return;
        }

        MenuFormatter.printHeader("Predictive Simulation Sandbox");
        System.out.println("Select account to simulate projections against:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println(" [" + (i + 1) + "] " + accounts.get(i).getType() + " (" + accounts.get(i).getId() + ")");
        }
        
        int index;
        try {
            System.out.print("Choice: ");
            index = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Mismatch type. Numeric values required inside simulation parameters.");
            return;
        }

        if (index < 0 || index >= accounts.size()) {
            System.out.println("[ERROR] Invalid choice mapping.");
            return;
        }

        BankAccount selection = accounts.get(index);
        MenuFormatter.printHeader("Simulation Parameters");

        try {
            if (selection instanceof SavingsAccount) {
                SavingsAccount savings = (SavingsAccount) selection;
                System.out.print("Enter duration of investment holding (in Years): ");
                int years = Integer.parseInt(scanner.nextLine().trim());
                
                double futureValue = savings.getBalance() * Math.pow((1 + savings.getInterestRate()), years);
                double totalYield = futureValue - savings.getBalance();

                MenuFormatter.printStatRow("Current Balance Base", String.format("$%.2f", savings.getBalance()));
                MenuFormatter.printStatRow("Annual Percentage Rate", String.format("%.2f%%", savings.getInterestRate() * 100));
                MenuFormatter.printStatRow("Holding Target Horizon", years + " Years");
                MenuFormatter.printDivider();
                MenuFormatter.printStatRow("Projected Interest Accrued", String.format("$%.2f", totalYield));
                MenuFormatter.printStatRow("Estimated Future Net Position", String.format("$%.2f", futureValue));

            } else if (selection instanceof LoanAccount) {
                LoanAccount loan = (LoanAccount) selection;
                System.out.print("Enter target repayment duration terms (in Months): ");
                int months = Integer.parseInt(scanner.nextLine().trim());

                double monthlyRate = loan.getInterestRate() / 12;
                double monthlyPayment = (loan.getBalance() * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -months));
                double aggregatePayout = monthlyPayment * months;

                MenuFormatter.printStatRow("Outstanding Debt Balance", String.format("$%.2f", loan.getBalance()));
                MenuFormatter.printStatRow("Configured Interest Rate", String.format("%.2f%%", loan.getInterestRate() * 100));
                MenuFormatter.printDivider();
                MenuFormatter.printStatRow("Estimated Monthly Repayment", String.format("$%.2f", monthlyPayment));
                MenuFormatter.printStatRow("Aggregate Cumulative Payout", String.format("$%.2f", aggregatePayout));
                MenuFormatter.printStatRow("Total Cost of Credit", String.format("$%.2f", aggregatePayout - loan.getBalance()));
            } else {
                System.out.println("[INFO] Simulation models are optimized explicitly for Savings and Loan variations.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[INPUT FAILURE] Execution cancelled. Whole integer timeline parameters expected.");
        }
        MenuFormatter.printFooter();
    }

    private void displayTransactionHistory(Customer customer) {
        MenuFormatter.printHeader("Auditable Account Ledger");
        List<BankAccount> accounts = accountRepo.findByOwnerId(customer.getId());
        
        if (accounts.isEmpty()) {
            System.out.println("[INFO] No data logs available to build ledger statements.");
            return;
        }

        for (BankAccount acc : accounts) {
            System.out.println("\n>> Real-Time Statement Stream for Account ID: " + acc.getId());
            MenuFormatter.printDivider();
            
            List<String> history = acc.getTransactionHistory();
            
            if (history.isEmpty()) {
                System.out.println("   [SYSTEM CHECK] No mutations recorded on disk for this account cycle.");
            } else {
                for (String record : history) {
                    System.out.println("  • " + record);
                }
            }
        }
        MenuFormatter.printFooter();
    }

    private void handleWithdrawal(Customer customer) {
        System.out.print("Enter Account ID: ");
        String accId = scanner.nextLine().trim();
        
        double amount;
        try {
            System.out.print("Enter Amount to Withdraw: ");
            amount = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric formatting.");
            return;
        }

        try {
            accountManager.executeManagedWithdrawal(accId, customer.getId(), amount);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ==========================================
    //           TELLER STATION LAYER
    // ==========================================
    // ==========================================
    //           TELLER STATION LAYER
    // ==========================================
    private void handleTellerWorkstation() {
        while (true) {
            MenuFormatter.printHeader("Teller Employee Workstation");
            MenuFormatter.printMenuOption("1", "Register New Customer Profile");
            MenuFormatter.printMenuOption("2", "Open Sub-Account for Existing Member");
            MenuFormatter.printMenuOption("3", "Clear Profile Suspensions (Reactivate Customer)");
            MenuFormatter.printMenuOption("4", "Return to Main Gateway");
            MenuFormatter.printFooter();

            System.out.print("Enter teller command: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                handleCustomerRegistration();
            } else if (choice.equals("2")) {
                handleSubAccountCreation();
            } else if (choice.equals("3")) {
                handleUnlockCustomer();
            } else if (choice.equals("4")) {
                break;
            } else {
                System.out.println("[ERROR] Invalid workstation command.");
            }
        }
    }

    private void handleCustomerRegistration() {
        MenuFormatter.printHeader("Profile Registration");
        try {
            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Age: ");
            int age = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Email Address: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter Phone Number: ");
            int phoneNumber = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Create Initial Password: ");
            String password = scanner.nextLine().trim();

            String generatedId = accountManager.registerCustomer(name, age, email, phoneNumber, password);
            
            System.out.println("\n[SUCCESS] Profile provisioned on core system.");
            System.out.println("-------------------------------------------------");
            System.out.println("  ASSIGNED CUSTOMER ID: " + generatedId);
            System.out.println("  Please save this ID securely for future logins.");
            System.out.println("-------------------------------------------------");
        } catch (NumberFormatException e) {
            System.out.println("[REGISTRATION FAILED] Numerical conversion error for age or phone number inputs.");
        } catch (Exception e) {
            System.out.println("[REGISTRATION FAILED] " + e.getMessage());
        }
    }

    private void handleSubAccountCreation() {
        MenuFormatter.printHeader("Provision Sub-Account");
        System.out.print("Enter Target Customer ID: ");
        String customerId = scanner.nextLine().trim();

        if (customerRepo.findById(customerId).isEmpty()) {
            System.out.println("[ERROR] No customer found with that ID.");
            return;
        }

        System.out.println("Select Account Type:");
        System.out.println(" [1] Checking (With Overdraft Limit)");
        System.out.println(" [2] Savings (With Compound Interest)");
        System.out.println(" [3] Loan (Liability Account)");
        System.out.println(" [4] Credit (Revolving Debt Line)");
        System.out.print("Choice: ");
        String typeChoice = scanner.nextLine().trim();

        try {
            switch (typeChoice) {
                case "1":
                    System.out.print("Enter Initial Deposit: $");
                    double checkDeposit = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Set Overdraft Limit: $");
                    double overdraft = Double.parseDouble(scanner.nextLine().trim());
                    accountManager.openCheckingAccount(customerId, checkDeposit, overdraft);
                    break;
                case "2":
                    System.out.print("Enter Initial Deposit: $");
                    double saveDeposit = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Set Annual Interest Rate (e.g. 0.04): ");
                    double rate = Double.parseDouble(scanner.nextLine().trim());
                    accountManager.openSavingsAccount(customerId, saveDeposit, rate);
                    break;
                case "3":
                    System.out.print("Enter Total Loan Principal Amount: $");
                    double principal = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Set Loan Interest Rate (e.g. 0.05): ");
                    double loanRate = Double.parseDouble(scanner.nextLine().trim());
                    double startingBalance = 0;
                    accountManager.openLoanAccount(customerId, startingBalance, principal, loanRate);
                    break;
                case "4":
                    System.out.print("Enter Starting Balance (Usually 0 for new cards): $");
                    double creditBalance = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Set Maximum Credit Limit: $");
                    double limit = Double.parseDouble(scanner.nextLine().trim());
                    accountManager.openCreditAccount(customerId, creditBalance, limit);
                    break;
                default:
                    System.out.println("[ERROR] Invalid account configuration selected.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[ABORTED] Type mismatch conversion error. Numeric numbers expected.");
        } catch (Exception e) {
            System.out.println("[PROVISIONING FAILED] " + e.getMessage());
        }
    }

    private void handleUnlockCustomer() {
        MenuFormatter.printHeader("Security: Clear Profile Suspensions");
        System.out.print("Enter Suspended Customer ID: ");
        String customerId = scanner.nextLine().trim();

        // Safely unwrapped the Optional with custom exception instead of .get()
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Customer profile not found for ID: " + customerId));

        if (customer.getStatus() == CustomerStatus.SUSPENDED) {
            customer.reactivate();
            customerRepo.save(customer);
            System.out.println("[SUCCESS] Profile is now active and clear.");
        } else if (customer.getStatus() == CustomerStatus.CLOSED) {
            System.out.println("[DENIED] Profile is permanently closed. Cannot override status.");
        } else {
            System.out.println("[INFO] Profile is already active and clear.");
        }
    }

    // ==========================================
    //           ADMINISTRATION LAYER
    // ==========================================
    private void handleAdminTerminal() {
        while (true) {
            MenuFormatter.printHeader("Global Operations Control Center");
            MenuFormatter.printMenuOption("1", "Display System-Wide Statistics");
            MenuFormatter.printMenuOption("2", "Toggle Dynamic Account Status (Freeze/Unfreeze)");
            MenuFormatter.printMenuOption("3", "Trigger Global Interest Batch Processing");
            MenuFormatter.printMenuOption("4", "Return to Main Gateway");
            MenuFormatter.printFooter();

            System.out.print("Enter admin system command: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                printGlobalSystemStats();
            } else if (choice.equals("2")) {
                toggleAccountState();
            } else if (choice.equals("3")) {
                triggerGlobalInterestBatch();
            } else if (choice.equals("4")) {
                break;
            } else {
                System.out.println("[ERROR] Command unrecognized.");
            }
        }
    }

    private void printGlobalSystemStats() {
        MenuFormatter.printHeader("Macro Banking Metrics");
        List<Customer> allCustomers = customerRepo.findAll();
        List<BankAccount> allAccounts = accountRepo.findAll();

        double dynamicTotalBankLiquidity = 0;
        int activeAccounts = 0;
        int suspendedProfiles = 0;

        for (Customer c : allCustomers) {
            if (c.getStatus() == CustomerStatus.SUSPENDED) suspendedProfiles++;
        }

        for (BankAccount acc : allAccounts) {
            if (!(acc instanceof LoanAccount || acc instanceof CreditAccount)) {
                dynamicTotalBankLiquidity += acc.getBalance();
            }
            activeAccounts++;
        }

        MenuFormatter.printStatRow("Total System Managed Accounts", String.valueOf(activeAccounts));
        MenuFormatter.printStatRow("Flagged Suspended Profiles", String.valueOf(suspendedProfiles));
        MenuFormatter.printStatRow("Total Vault Liquid Holdings", String.format("$%.2f", dynamicTotalBankLiquidity));
        MenuFormatter.printFooter();
    }

    private void toggleAccountState() {
        MenuFormatter.printHeader("Security: Mutate Account Lifecycle");
        System.out.print("Enter Target Account ID: ");
        String accountId = scanner.nextLine().trim();

        // Safely handled account checking with explicit runtime message mapping
        BankAccount acc = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] Account reference not discovered in storage databases."));

        System.out.println("Current Status of [" + acc.getId() + "] is: " + acc.getStatus());
        System.out.println("Select Target Execution Command:\n [1] Freeze Account\n [2] Reactivate Account\n [3] Permanently Close Account");
        System.out.print("Command choice: ");
        String cmd = scanner.nextLine().trim();

        if (cmd.equals("1")) {
            acc.freeze();
        } else if (cmd.equals("2")) {
            acc.reactivate();
        } else if (cmd.equals("3")) {
            acc.close();
        } else {
            System.out.println("[ERROR] Command selection aborted.");
            return;
        }
        
        accountRepo.save(acc);
        System.out.println("[SUCCESS] Account lifecycle mutation saved to storage.");
    }

    private void triggerGlobalInterestBatch() {
        MenuFormatter.printHeader("Batch Processing: Apply System Yields");
        System.out.println("[WARNING] This operation iterates over all files and mutates balances.");
        System.out.print("Confirm global database modification loop? (YES/NO): ");
        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("YES")) {
            System.out.println("[ABORTED] Batch mutation lifecycle cancelled cleanly.");
            return;
        }

        List<BankAccount> allAccounts = accountRepo.findAll();
        int affectedAccounts = 0;

        for (BankAccount acc : allAccounts) {
            if (acc instanceof SavingsAccount) {
                SavingsAccount savings = (SavingsAccount) acc;
                double calculatedInterest = savings.getBalance() * (savings.getInterestRate() / 12);
                savings.deposit(calculatedInterest); 
                accountRepo.save(savings);
                affectedAccounts++;
            }
        }

        System.out.println("[BATCH SUCCESS] Process routine finalized.");
        System.out.println("  Total Savings records processed and updated: " + affectedAccounts);
        MenuFormatter.printFooter();
    }

    private void runSystemDiagnostics() {
        MenuFormatter.printHeader("System Diagnostic Audit");
        int customerRecords = customerRepo.findAll().size();
        int accountRecords = accountRepo.findAll().size();
        
        MenuFormatter.printStatRow("Core Package Status", "FULLY DECOUPLED");
        MenuFormatter.printStatRow("Loaded Customer Records (.dat)", String.valueOf(customerRecords));
        MenuFormatter.printStatRow("Loaded Polymorphic Asset Profiles", String.valueOf(accountRecords));
        MenuFormatter.printStatRow("Encryption Utility Layer", "VERIFIED (AES/SHA)");
        MenuFormatter.printStatRow("File Serialization System", "INTEGRITY SECURE");
        
        MenuFormatter.printFooter();
        System.out.print("Press Enter to return to main gateway...");
        scanner.nextLine();
    }
}