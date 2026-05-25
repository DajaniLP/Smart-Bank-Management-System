package systems;

import enums.CustomerStatus;
import exceptions.AccountLockedException;
import exceptions.CustomerNotFoundException;
import java.io.Serializable;
import models.people.Customer;
import repositories.CustomerRepository;
import utils.EncryptionUtils;

public class LoginSystem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CustomerRepository customerRepo;

    public LoginSystem(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    public boolean authenticate(String customerId, String password) throws AccountLockedException {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Authentication failed. Customer profile not found."));

        if (customer.getStatus() == CustomerStatus.SUSPENDED) {
            throw new AccountLockedException("[ACCESS DENIED] Profile " + customerId + " is structurally locked or suspended.");
        }
        
        if (customer.getStatus() == CustomerStatus.CLOSED) {
            System.out.println("[ERROR] Cannot access a permanently closed profile.");
            return false;
        }

        String hashedInput = EncryptionUtils.hashPassword(password);
        if (customer.getPassword().equals(hashedInput)) {
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