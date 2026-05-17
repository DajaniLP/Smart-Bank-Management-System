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
    
    public BankAccount(String ownerId, double startingBalance) {
        // Generates unique ID using UUID, 1-6 max characters, uppercase
        this.id = "ACC-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        this.ownerId = ownerId;
        this.balance = startingBalance;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.transactionHistory = new ArrayList<>();
    }

    // transaction methods

    @Override
    public void deposit(double amount) {
        validateAccountActive();
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }

        this.balance += amount;
        recordTransaction("Deposited: $" + String.format("%.2f", amount));

        System.out.println("-------------------------------------------------");
        System.out.println("» DEPOSIT SUCCESSFUL");
        System.out.println("  Amount Added: +$" + String.format("%.2f", amount));
        System.out.println("  New Balance:   $" + String.format("%.2f", balance));
        System.out.println("-------------------------------------------------");        
    }

    @Override
    public void withdraw(double amount) {
        validateAccountActive();
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > this.balance) {
            throw new InsufficientFundsException("Insufficient funds. Current balance: $" + String.format("%.2f", this.balance));
        }
        
        this.balance -= amount;
        recordTransaction("Withdrew: $" + String.format("%.2f", amount));
        
        System.out.println("-------------------------------------------------");
        System.out.println("» WITHDRAWAL SUCCESSFUL");
        System.out.println("  Amount Deducted: -$" + String.format("%.2f", amount));
        System.out.println("  New Balance:     $" + String.format("%.2f", balance));
        System.out.println("-------------------------------------------------");
    }

    @Override
    public void transfer(double amount, BankAccount targetAccount) {
        validateAccountActive();
        if (amount <= 0) {
            throw new IllegalArgumentException("[ERROR] Transfer amount must be positive.");
        }
        
        if (amount > this.balance) {
            throw new InsufficientFundsException("[ERROR] Insufficient funds. Current balance: $" 
            + String.format("%.2f", this.balance));
        }

        this.balance -= amount;
        targetAccount.setBalance(targetAccount.getBalance() + amount);
        this.lastTransactionAt = LocalDateTime.now();

        this.transactionHistory.add("Transferred $" + String.format("%.2f", amount) 
        + " to [" + targetAccount.getId() + "] at " + lastTransactionAt.format(DATE_FORMATTER));

        System.out.println("-------------------------------------------------");
        System.out.println("» TRANSFER SUCCESSFUL");
        System.out.println("  Amount Sent: -$" + String.format("%.2f", amount));
        System.out.println("  Recipient:    " + targetAccount.getId());
        System.out.println("  New Balance:  $" + String.format("%.2f", balance));
        System.out.println("-------------------------------------------------");        
    }

    // audit methods

    @Override
    public LocalDateTime getCreatedTimestamp() {
        return this.createdAt;
    }

    @Override
    public LocalDateTime getLastUpdatedTimestamp() {
        return this.lastTransactionAt;
    }

    // account status management methods

    public void close() {
        switch (status) {
            case CLOSED -> System.out.println("[ERROR] Account Status Conflict: This account is already closed.");
            default -> {
                this.status = AccountStatus.CLOSED;
                System.out.println("[STATUS UPDATE] Account [" + id + "] has been successfully CLOSED.");
            }
        }
    }

    public void freeze() {
        switch (status) {
            case FROZEN -> System.out.println("[ERROR] Account Status Conflict: This account is already frozen.");
            case CLOSED -> System.out.println("[ERROR] Account Status Conflict: Cannot freeze a closed account.");
            default -> {
                this.status = AccountStatus.FROZEN;
                System.out.println("[STATUS UPDATE] Account [" + id + "] has been successfully FROZEN.");
            }
        }
    }

    public void reactivate() {
        switch (status) {
            case ACTIVE -> System.out.println("[ERROR] Account Status Conflict: This account is already active.");
            case CLOSED -> System.out.println("[ERROR] Account Status Conflict: Cannot reactivate a closed account.");
            default -> {
                this.status = AccountStatus.ACTIVE;
                System.out.println("[STATUS UPDATE] Account [" + id + "] has been successfully REACTIVATED.");
            }    
        }
    }

    // helper methods

    private void validateAccountActive() {
        if (this.status == AccountStatus.FROZEN) {
            throw new AccountLockedException("[ERROR] Transaction Denied. Account [" + id + "] is currently FROZEN/LOCKED");
        }

        if (this.status == AccountStatus.CLOSED) {
            throw new AccountLockedException("[ERROR] Transaction Denied. Account [\" + id + \"] is permanently CLOSED.");
        }


    }

    protected void recordTransaction(String description) {
        this.lastTransactionAt = LocalDateTime.now();
        this.transactionHistory.add(description + " on " + this.lastTransactionAt.format(DATE_FORMATTER));
    }

    // getters

    public String getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public double getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public LocalDateTime getLastTransactionAt() { return lastTransactionAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public abstract AccountType getType();

    public List<String> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    //setters  

    public void setBalance(double balance) { 
        this.balance = balance; 
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    // display info 
    public void displayInfo() {
        String createdStr = createdAt.format(DATE_FORMATTER);
        String lastTxStr = (lastTransactionAt != null) ? lastTransactionAt.format(DATE_FORMATTER) : "No transaction activity recorded";

        System.out.println("=================================================");
        System.out.println("                ACCOUNT AUDIT REPORT             ");
        System.out.println("=================================================");
        System.out.printf("  %-22s : %s\n", "Account ID", id);
        System.out.printf("  %-22s : %s\n", "Account Type", getType());        
        System.out.printf("  %-22s : %s\n", "Account Status", status);
        System.out.printf("  %-22s : $%s\n", "Current Balance", String.format("%.2f", balance));
        System.out.printf("  %-22s : %s\n", "Created Timestamp", createdStr);
        System.out.printf("  %-22s : %s\n", "Last Active Timestamp", lastTxStr);
        System.out.println("=================================================");
        System.out.println("");
    }
}