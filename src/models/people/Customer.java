package models.people;

import enums.*;
import interfaces.Auditable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import utils.EncryptionUtils;
import utils.ValidationUtils;

public class Customer extends Person implements Auditable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String id;
    private String password;
    private CustomerStatus status;
    private MembershipTier tier;

    private final LocalDateTime createdAt;
    private LocalDateTime lastLogin; 

    public Customer(String id, String name, int age, String email, int phoneNumber, String password) {
        super(name, age, email, phoneNumber); 

        if (!ValidationUtils.isValidEmail(email)) {
            throw new IllegalArgumentException("[ERROR] Registration Rejected: Invalid email structural format.");
        }

        this.id = id; 
        this.password = EncryptionUtils.hashPassword(password);
        this.status = CustomerStatus.ACTIVE;
        this.tier = MembershipTier.REGULAR;
        this.createdAt = LocalDateTime.now();
    }


    public String getId() { return id; }
    public String getPassword() { return password; }
    public CustomerStatus getStatus() { return status; }
    public MembershipTier getMembershipTier() { return tier; }

    public void suspend() { this.status = CustomerStatus.SUSPENDED; }
    public void reactivate() { this.status = CustomerStatus.ACTIVE; }
    public void setMembershipTier(MembershipTier tier) { this.tier = tier; }
    
    public void recordLogin() { this.lastLogin = LocalDateTime.now(); }

    @Override
    public LocalDateTime getCreatedTimestamp() { return createdAt; }
    @Override
    public LocalDateTime getLastUpdatedTimestamp() { return lastLogin; }

    @Override
    public void displayInfo() {
        String createdStr = createdAt.format(DATE_FORMATTER);
        String lastLoginStr = (lastLogin != null) ? lastLogin.format(DATE_FORMATTER) : "No session activity recorded";

        System.out.println("=================================================");
        System.out.println("                CUSTOMER COMPREHENSIVE PROFILE   ");
        System.out.println("=================================================");
        System.out.printf("  %-22s : %s\n", "Customer ID", id);
        System.out.printf("  %-22s : %s\n", "Legal Name", getName());
        System.out.printf("  %-22s : %d years old\n", "Age Profile", getAge());
        System.out.printf("  %-22s : %s\n", "Email Address", getEmail());
        System.out.printf("  %-22s : %d\n", "Phone Registration", getPhoneNumber());
        System.out.printf("  %-22s : %s\n", "Profile Status", status);
        System.out.printf("  %-22s : %s\n", "Membership Tier", tier);
        System.out.printf("  %-22s : %s\n", "Profile Creation", createdStr);
        System.out.printf("  %-22s : %s\n", "Last Active Session", lastLoginStr);
        System.out.println("=================================================");
    }
}