package managers;

import exceptions.CustomerNotFoundException;
import interfaces.CrudRepository;
import java.io.Serializable;
import java.util.Optional;
import models.people.Customer;

public class CustomerManager implements Serializable {
    private static final long serialVersionUID = 1L;

    // Taps into your CrudRepository pattern
    private final CrudRepository<Customer, String> customerRepo;

    public CustomerManager(CrudRepository<Customer, String> customerRepo) {
        this.customerRepo = customerRepo;
    }

    /*
      Finds a customer profile by its unique Person ID string.
     */
    public Optional<Customer> findCustomer(String customerId) {
        return customerRepo.findById(customerId);
    }

    /*
     Triggers a cascading administrative suspension across a customer's profile 
     and freezes all linked financial assets.
     */
    public void suspendCustomer(String customerId) {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Customer profile not found for ID: " + customerId));

        customer.suspend(); // Executes your custom cascading logic
        customerRepo.save(customer); // Flushes changes safely back to disk
    }

    
    // Reactivates a suspended customer profile and clears locks on their linked financial assets.
    
    public void reactivateCustomer(String customerId) {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("[ERROR] Customer profile not found for ID: " + customerId));

        customer.reactivate(); // Executes your custom cascading activation logic
        customerRepo.save(customer); // Flushes changes safely back to disk
    }
}