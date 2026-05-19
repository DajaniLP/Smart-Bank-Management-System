package managers;

import exceptions.CustomerNotFoundException;
import interfaces.CrudRepository;
import java.io.Serializable;
import java.util.Optional;
import models.people.Customer;

public class CustomerManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CrudRepository<Customer, String> customerRepo;

    public CustomerManager(CrudRepository<Customer, String> customerRepo) {
        this.customerRepo = customerRepo;
    }

    public Optional<Customer> findCustomer(String customerId) {
        return customerRepo.findById(customerId);
    }

    public void suspendCustomer(String customerId) {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Customer profile not found for ID: " + customerId));

        customer.suspend(); 
        customerRepo.save(customer); 
        System.out.println("[SUCCESS] Profile status switched: " + customerId + " is now SUSPENDED.");
    }

    public void reactivateCustomer(String customerId) {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Customer profile not found for ID: " + customerId));

        customer.reactivate(); 
        customerRepo.save(customer); 
        System.out.println("[SUCCESS] Profile status switched: " + customerId + " is now ACTIVE.");
    }
}