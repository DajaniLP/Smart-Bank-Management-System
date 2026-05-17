package repositories;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import models.people.Customer;

public class CustomerRepository {

    private static final String FILE_PATH = "customers.dat";
    private List<Customer> customerDatabase;

    public CustomerRepository() {
        this.customerDatabase = new ArrayList<>();
        ensureFileExists();
        loadAll(); 
    }

    // Ensures that the 'data' directory and file exist without crashing
    private void ensureFileExists() {
        try {
            File file = new File(FILE_PATH);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs(); // Creates data folder if missing
            }
            if (!file.exists()) {
                file.createNewFile();
                saveAll(); // Initializes the file with an empty ArrayList
            }
        } catch (IOException e) {
            System.out.println("[CRITICAL ERROR] Could not initialize customer file system: " + e.getMessage());
        }
    }

    // core operations

    // Save or update a customer record
    public void save(Customer customer) {
        // If customer already exists in memory, remove the stale version first
        customerDatabase.removeIf(c -> c.getId().equals(customer.getId()));
        customerDatabase.add(customer);
        saveAll(); // Flush the updated list into the physical file
    }

    // Find a customer profile using their Unique ID string
    public Optional<Customer> findById(String id) {
        for (Customer customer : customerDatabase) {
            if (customer.getId().equals(id)) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }

    // Return all customers currently tracked
    public List<Customer> findAll() {
        return new ArrayList<>(customerDatabase);
    }

    // Delete a customer profile by ID
    public void deleteById(String id) {
        customerDatabase.removeIf(c -> c.getId().equals(id));
        saveAll();
    }

    // file IO mechanics

    // Serializes the entire ArrayList block directly into a single file layer
    private void saveAll() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(customerDatabase);
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to serialize customer data: " + e.getMessage());
        }
    }

    // Deserializes the file data back into a usable working ArrayList context
    @SuppressWarnings("unchecked")
    private void loadAll() {
        File file = new File(FILE_PATH);
        if (file.length() == 0) return; // Drop execution if file is currently blank

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            this.customerDatabase = (List<Customer>) ois.readObject();
        } catch (EOFException e) {
            // End of file reached cleanly, do nothing
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[ERROR] Failed to deserialize customer files: " + e.getMessage());
        }
    }
}