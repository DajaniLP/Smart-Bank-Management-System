package models.people;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

import enums.*;
import models.accounts.BankAccount;

public abstract class Customer extends Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String password;
    private CustomerStatus status;
    private MembershipTier tier;

    private final LocalDateTime createdAt;
    private LocalDateTime lastLogin; 

    private final List<BankAccount> accounts;

    public Customer(String name, int age, String email, int phoneNumber, String password) {

        super(name, age, email, phoneNumber);

        this.password = password;
        this.status = CustomerStatus.ACTIVE;
        this.tier = MembershipTier.REGULAR;
        this.createdAt = LocalDateTime.now();
        this.lastLogin = LocalDateTime.now();
        this.accounts = new ArrayList<>();
    }

    // account management methods

    public void addAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalStateException("[ERROR] Account Assignment Denied: Customer profile is permanently closed.");            
        }
        this.accounts.add(account);
    }

    public void close() {
        switch (status) {
            case CLOSED -> System.out.println("[ERROR] Customer Status Conflict: This profile is already closed.");
            default -> {
                this.status = CustomerStatus.CLOSED;
                System.out.println("[STATUS UPDATE] Customer [" + getId() + "] profile has been CLOSED.");

                for (BankAccount account : accounts) {
                    account.close();
                }
            }
        }
    }

    public void suspend() {
        switch (status) {
            case SUSPENDED -> System.out.println("[ERROR] Customer Status Conflict: This profile is already suspended.");
            case CLOSED -> System.out.println("[ERROR] Customer Status Conflict: Cannot suspend a closed profile."); 
            default -> {
                this.status = CustomerStatus.SUSPENDED;
                System.out.println("[STATUS UPDATE] Customer [" + getId() + "] profile has been SUSPENDED.");

                for (BankAccount account : accounts) {
                    account.freeze();
                }
            }
        }
    }

    public void reactivate() {
        switch (status) {
            case ACTIVE -> System.out.println("[ERROR] Customer Status Conflict: This profile is already active.");
            case CLOSED -> System.out.println("[ERROR] Customer Status Conflict: Cannot reactivate a closed profile.");
            default -> {
                this.status = CustomerStatus.ACTIVE;
                System.out.println("[STATUS UPDATE] Customer [" + getId() + "] profile has been REACTIVATED.");

                for (BankAccount account : accounts) {
                    account.reactivate();
                }
            }
        }
    }

    public void recordLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    // getters

    public String getPassword() { return password; }
    public CustomerStatus getStatus() { return status; }
    public MembershipTier getMembershipTier() { return tier; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLogin() { return lastLogin; }

    public int getTotalAccounts() {
        return accounts.size();
    }

    // data protection method (shout out to Gemini for this one)

    public List<BankAccount> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    // setters

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatus(CustomerStatus customerStatus) {
        this.status = customerStatus;
    }
    
    public void setMembershipTier(MembershipTier tier) {
        this.tier = tier;
    }    

    // display info

    @Override
    public void displayInfo() {
        String createdStr = createdAt.format(DATE_FORMATTER);
        String lastLoginStr = (lastLogin != null) ? lastLogin.format(DATE_FORMATTER) : "No record of profile access";

        System.out.println("=================================================");
        System.out.println("                CUSTOMER AUDIT PROFILE           ");
        System.out.println("=================================================");
        System.out.printf("  %-22s : %s\n", "Customer ID", getId());
        System.out.printf("  %-22s : %s\n", "Legal Name", getName());
        System.out.printf("  %-22s : %s\n", "Age Profile", getAge());
        System.out.printf("  %-22s : %s\n", "Email Address", getEmail());
        System.out.printf("  %-22s : %s\n", "Phone Registration", getPhoneNumber());
        System.out.printf("  %-22s : %s\n", "Profile Status", getStatus());
        System.out.printf("  %-22s : %s\n", "Membership Tier", getMembershipTier());
        System.out.printf("  %-22s : %s\n", "Profile Creation", createdStr);
        System.out.printf("  %-22s : %s\n", "Last Login", lastLoginStr);
        System.out.printf("  %-22s : %d\n", "Linked Asset Accounts", getTotalAccounts());
        System.out.println("=================================================");
        System.out.println("");
        System.out.println("                LINKED FINANCIAL ASSETS          ");
        System.out.println("=================================================");

        if (accounts.isEmpty()) {
            System.out.println("  [NOTICE] No financial accounts registered under this identity.");
            System.out.println("=================================================\n");
        } else {
            for (BankAccount account : accounts) {
                account.displayInfo();
            }
        }
    }
} 
