package repositories;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import models.people.Customer;

public class CustomerRepository implements interfaces.CrudRepository<Customer, String> {

    private static final String FILE_PATH = "customers.dat";
    private List<Customer> customerDatabase;

    public CustomerRepository() {
        this.customerDatabase = new ArrayList<>();
        ensureFileExists();
        loadAll(); 
    }

    private void ensureFileExists() {
        try {
            File file = new File(FILE_PATH);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs(); 
            }
            if (!file.exists()) {
                // Instantly initialize with a serialized empty arraylist header
                saveAll(); 
            }
        } catch (Exception e) {
            System.out.println("[CRITICAL ERROR] Could not initialize customer file system: " + e.getMessage());
        }
    }

    @Override
    public void save(Customer customer) {
        customerDatabase.removeIf(c -> c.getId().equals(customer.getId()));
        customerDatabase.add(customer);
        saveAll(); 
    }

    @Override
    public Optional<Customer> findById(String id) {
        for (Customer customer : customerDatabase) {
            if (customer.getId().equals(id)) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customerDatabase);
    }

    @Override
    public void deleteById(String id) {
        customerDatabase.removeIf(c -> c.getId().equals(id));
        saveAll();
    }

    private void saveAll() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(customerDatabase);
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to serialize customer data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAll() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            this.customerDatabase = new ArrayList<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            this.customerDatabase = (List<Customer>) ois.readObject();
        } catch (EOFException | StreamCorruptedException e) {
            // Self-heal file if stream data header is empty or broken
            this.customerDatabase = new ArrayList<>();
            saveAll();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[ERROR] Failed to deserialize customer files: " + e.getMessage());
            this.customerDatabase = new ArrayList<>();
        }
    }
}