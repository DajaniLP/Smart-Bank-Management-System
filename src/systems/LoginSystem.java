package systems;

import enums.CustomerStatus;
import exceptions.AccountLockedException;
import exceptions.CustomerNotFoundException;
import java.io.Serializable;
import models.people.Customer;
import repositories.CustomerRepository;

public class LoginSystem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CustomerRepository customerRepo;

    public LoginSystem(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    // Authenticate a customer profile against their password credentials
    public boolean authenticate(String customerId, String password) throws AccountLockedException {
        // Replaced unsafe conditional tracking with your Custom Exceptions
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Authentication failed. Customer profile not found."));

        // Enforce administrative suspensions immediately
        if (customer.getStatus() == CustomerStatus.SUSPENDED) {
            throw new AccountLockedException("[ACCESS DENIED] Profile " + customerId + " is structurally locked or suspended.");
        }
        
        if (customer.getStatus() == CustomerStatus.CLOSED) {
            System.out.println("[ERROR] Cannot access a permanently closed profile.");
            return false;
        }

        // Validate password match
        if (customer.getPassword().equals(password)) {
            customer.recordLogin();
            customerRepo.save(customer);
            System.out.println("[SUCCESS] Gateway clearance granted for " + customer.getName());
            return true;
        } else {
            System.out.println("[ERROR] Invalid password credentials. Access denied.");
            return false;
        }
    }
}