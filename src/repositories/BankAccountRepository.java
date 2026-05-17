package repositories;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import models.accounts.BankAccount;

public class BankAccountRepository {
    private static final String FILE_PATH = "accounts.dat";
    private List<BankAccount> accountDatabase;

    public BankAccountRepository() {
        this.accountDatabase = new ArrayList<>();
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
                file.createNewFile();
                saveAll();
            }
        } catch (IOException e) {
            System.out.println("[CRITICAL ERROR] Could not initialize account file system: " + e.getMessage());
        }
    }

    // core operations

    public void save(BankAccount account) {
        accountDatabase.removeIf(a -> a.getId().equals(account.getId()));
        accountDatabase.add(account);
        saveAll();
    }

    public Optional<BankAccount> findById(String id) {
        for (BankAccount account : accountDatabase) {
            if (account.getId().equals(id)) {
                return Optional.of(account);
            }
        }
        return Optional.empty();
    }

    public List<BankAccount> findByOwnerId(String ownerId) {
        List<BankAccount> ownerAccounts = new ArrayList<>();
        for (BankAccount account : accountDatabase) {
            if (account.getOwnerId().equals(ownerId)) {
                ownerAccounts.add(account);
            }
        }
        return ownerAccounts;
    }

    public List<BankAccount> findAll() {
        return new ArrayList<>(accountDatabase);
    }

    public void deleteById(String id) {
        accountDatabase.removeIf(a -> a.getId().equals(id));
        saveAll();
    }

    // file IO mechanics

    private void saveAll() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(accountDatabase);
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to serialize account data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAll() {
        File file = new File(FILE_PATH);
        if (file.length() == 0) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            this.accountDatabase = (List<BankAccount>) ois.readObject();
        } catch (EOFException e) {
            // End of file safely reached
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[ERROR] Failed to deserialize account files: " + e.getMessage());
        }
    }
}