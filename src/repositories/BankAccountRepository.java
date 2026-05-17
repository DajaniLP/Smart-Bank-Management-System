package repositories;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import models.accounts.BankAccount;

public class BankAccountRepository implements interfaces.CrudRepository<BankAccount, String> {

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
                // Instantly initialize with a serialized empty arraylist header
                saveAll();
            }
        } catch (Exception e) {
            System.out.println("[CRITICAL ERROR] Could not initialize account file system: " + e.getMessage());
        }
    }

    @Override
    public void save(BankAccount account) {
        accountDatabase.removeIf(a -> a.getId().equals(account.getId()));
        accountDatabase.add(account);
        saveAll();
    }

    @Override
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

    @Override
    public List<BankAccount> findAll() {
        return new ArrayList<>(accountDatabase);
    }

    @Override
    public void deleteById(String id) {
        accountDatabase.removeIf(a -> a.getId().equals(id));
        saveAll();
    }

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
        if (!file.exists() || file.length() == 0) {
            this.accountDatabase = new ArrayList<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            this.accountDatabase = (List<BankAccount>) ois.readObject();
        } catch (EOFException | StreamCorruptedException e) {
            // Handle empty or corrupted headers safely by self-healing
            this.accountDatabase = new ArrayList<>();
            saveAll();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[ERROR] Failed to deserialize account files: " + e.getMessage());
            this.accountDatabase = new ArrayList<>();
        }
    }
}