package models.accounts;

import enums.*;
import exceptions.AccountLockedException;
import exceptions.InsufficientFundsException;
import interfaces.Auditable;
import interfaces.TransactionAction;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class BankAccount implements Serializable, Auditable, TransactionAction {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String id;
    private final String ownerId;
    private double balance;
    private AccountStatus status;
    
    private final LocalDateTime createdAt;
    private LocalDateTime lastTransactionAt;
    private final List<String> transactionHistory;
    private OwnershipType ownershipType = OwnershipType.PERSONAL;

    public BankAccount(String ownerId, double startingBalance) {
        this.id = "ACC-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        this.ownerId = ownerId;
        this.balance = startingBalance;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.transactionHistory = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public double getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public OwnershipType getOwnershipType() { return ownershipType; }
    public abstract AccountType getType();

    protected void setBalance(double balance) { this.balance = balance; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public void setOwnershipType(OwnershipType ownershipType) { this.ownershipType = ownershipType; }

    public void close() { this.status = AccountStatus.CLOSED; }
    public void freeze() { this.status = AccountStatus.FROZEN; }
    public void reactivate() { this.status = AccountStatus.ACTIVE; }

    @Override
    public LocalDateTime getCreatedTimestamp() { return createdAt; }
    @Override
    public LocalDateTime getLastUpdatedTimestamp() { return lastTransactionAt; }

    public List<String> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    protected void recordTransaction(String executionDetails) {
        this.lastTransactionAt = LocalDateTime.now();
        this.transactionHistory.add("[" + lastTransactionAt.format(DATE_FORMATTER) + "] " + executionDetails);
    }

    protected void validateAccountActive() {
        if (this.status == AccountStatus.LOCKED || this.status == AccountStatus.FROZEN) {
            throw new AccountLockedException("[ACCESS DENIED] Account " + id + " is locked or frozen.");
        }
        if (this.status == AccountStatus.CLOSED) {
            throw new IllegalStateException("[ERROR] Operations prohibited on closed account asset.");
        }
    }

    // Shared Default Polymorphic Implementations
    @Override
    public void deposit(double amount) {
        validateAccountActive();
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        this.balance += amount;
        recordTransaction("Deposited funds: +" + String.format("$%.2f", amount));
    }

    @Override
    public void withdraw(double amount) {
        validateAccountActive();
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (amount > this.balance) throw new InsufficientFundsException("[ERROR] Insufficient standard account funds.");
        this.balance -= amount;
        recordTransaction("Withdrew funds: -" + String.format("$%.2f", amount));
    }

    @Override
    public void transfer(double amount, BankAccount targetAccount) {
        if (targetAccount == null) throw new IllegalArgumentException("Target account cannot be null.");
        this.withdraw(amount);
        targetAccount.deposit(amount);
        this.recordTransaction("Transferred " + String.format("$%.2f", amount) + " to account: " + targetAccount.getId());
        targetAccount.recordTransaction("Received " + String.format("$%.2f", amount) + " from account: " + this.id);
    }

    // Polymorphic lifecycle hook for batch operations
    public void applyEndOfMonthInterest() {
        // Default: No-op for accounts that don't accrue interest
    }

    public void displayInfo() {
        String createdStr = createdAt.format(DATE_FORMATTER);
        String lastTxStr = (lastTransactionAt != null) ? lastTransactionAt.format(DATE_FORMATTER) : "No transaction activity recorded";

        System.out.println("=================================================");
        System.out.println("                ACCOUNT AUDIT REPORT             ");
        System.out.println("=================================================");
        System.out.printf("  %-22s : %s\n", "Account ID", id);
        System.out.printf("  %-22s : %s\n", "Account Type", getType());        
        System.out.printf("  %-22s : %s\n", "Account Status", status);
        System.out.printf("  %-22s : %s\n", "Ownership Classification", ownershipType);
        System.out.printf("  %-22s : $%s\n", "Current Balance", String.format("%.2f", balance));
        System.out.printf("  %-22s : %s\n", "Created Timestamp", createdStr);
        System.out.printf("  %-22s : %s\n", "Last Active Transaction", lastTxStr);
        System.out.println("=================================================");
    }
}