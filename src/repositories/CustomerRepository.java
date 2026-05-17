package repositories;

import java.io.*;
import java.util.*;

import models.people.Customer;

public class CustomerRepository {

    private static final String FILE_NAME = "customers.dat";

    public void save(List<Customer> customers) {
        try (ObjectOutputStream out =
                new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            out.writeObject(customers);

        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    public List<Customer> load() {
        try (ObjectInputStream in =
                new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            return (List<Customer>) in.readObject();

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}