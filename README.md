# Smart Bank Management System (V1.0.0 - Production Stabilized)

The Smart Bank Management System is a production-grade, terminal-based enterprise console application written in Java. The platform relies on a clean layered design and core object-oriented programming (OOP) principles to govern secure multi-user portal entries, perform transactional constraint validations across structural financial asset tiers, and preserve entity states across reboots using persistent binary object stream serialization.

---

## Technical Architecture & Design Layers

The application code is separated into dedicated domain packages to minimize class dependencies and isolate structural concerns:

* **`app` (Application Bootstrap)**
  * `Main.java`: The core application entry point. Instantiates binary files, establishes persistence repositories, injects layer dependencies, and boots up the user interface routing threads.

* **`enums` (Operational Invariants)**
  * `AccountStatus.java`: Tracks administrative flags for financial resources (`ACTIVE`, `LOCKED`, `FROZEN`, `CLOSED`).
  * `AccountType.java`: Groups specialized accounting options (`CHECKING`, `SAVINGS`, `CREDIT`, `LOAN`).
  * `CustomerStatus.java`: Flags profile usage rights (`ACTIVE`, `SUSPENDED`, `CLOSED`).
  * `MembershipTier.java`: Holds account validation parameters matching clients to transactional activity rules (`REGULAR`, `PREMIUM`, `VIP`).
  * `OwnershipType.java`: Models profile category rules (`PERSONAL`, `BUSINESS`).

* **`exceptions` (Domain Exception Tree)**
  * `BankException.java`: Abstract unchecked base runtime exception handling platform boundary validations.
  * `AccountLockedException.java`: Interrupts access routines targeting restricted or administratively suspended entities.
  * `CustomerNotFoundException.java`: Safely intercepts lookups returning null records.
  * `InsufficientFundsException.java`: Flags balance checking failures during manual debits or transfers.

* **`interfaces` (Architectural Design Contracts)**
  * `Auditable.java`: Tracks creation and modification timestamps across user profiles and account entities.
  * `CrudRepository.java`: Generic Data Access Object specification defining standards for storage transactions (`save`, `findById`, `findAll`, `deleteById`).
  * `TransactionAction.java`: Maps explicit code signatures for basic financial routines (`deposit`, `withdraw`, `transfer`).

* **`managers` (Business Calculation Engines)**
  * `CustomerManager.java`: Coordinates client profile updates and propagates cascading administrative suspensions.
  * `AccountManager.java`: Verifies configuration constraints when establishing portfolios, and acts as the relational bridge mapping synchronized cross-entity list caches.
  * `TransactionManager.java`: Handles cross-account balance adjustments, processing funds movements while logging ledger activity.

* **`models` (Structural Entities)**
  * `models/people/Person.java`: Abstract root class housing core client profiles, demographic strings, and contact fields.
  * `models/people/Customer.java`: Specialized class extending `Person` that tracks profile metadata, security fields, and holds encapsulated references to its matching asset portfolios.
  * `models/accounts/BankAccount.java`: Abstract base entity tracking unique routing keys, status flags, ownership tags, timestamps, and history streams.
  * `models/accounts/CheckingAccount.java`: Implements structural adjustments utilizing custom overdraft protection limits.
  * `models/accounts/SavingsAccount.java`: Computes periodic yield adjustments based on interest rates.
  * `models/accounts/CreditAccount.java`: Tracks outstanding credit usage against a fixed debt limit.
  * `models/accounts/LoanAccount.java`: Collects static liabilities and passes parent configurations cleanly through a corrected base matching constructor.

* **`repositories` (Data Persistence Layer)**
  * `CustomerRepository.java`: Interacts directly with `customers.dat` via object stream serialization. Features self-healing capabilities to reconstruct corrupt file headers.
  * `BankAccountRepository.java`: Interacts directly with `accounts.dat` via object stream serialization to load and persist financial asset states.

* **`systems` (Console Routing Subsystems)**
  * `LoginSystem.java`: Manages authentication loops, evaluating cryptographic matching operations securely by translating incoming keyboard plain text against salted record hashes.
  * `MenuFormatter.java`: Provides terminal layout utilities, including aligned display boxes, tabular layout tools, and console progress metrics.
  * `MenuSystem.java`: Controls application routing, providing isolated submenus for customers, tellers, and administrative operations.

* **`utils` (System Support Helpers)**
  * `EncryptionUtils.java`: Implements string mutations to protect stored credentials by calculating custom, reversible security hashes.
  * `ValidationUtils.java`: Evaluates user inputs against specific layout rules, including standard email string verification.

---

## Technical Highlights & OOP Strategy

1. **Polymorphic Invariants**: Common actions rely on shared interface signatures (`TransactionAction`). Subclasses override these methods to enforce specific behaviors, such as evaluating overdraft buffers on checking accounts or throwing unsupported operation exceptions to explicitly block direct withdrawals on loan accounts.
2. **Synchronized Cross-Entity Object Caching**: Solves data graph desynchronization issues common to dual-file binary architectures. When standalone `BankAccount` streams alter records, an internal relational mapping engine inside `AccountManager` bridges the `Customer` portfolio list automatically, clearing out stale caches before every profile menu load.
3. **Decoupled Data Architecture**: Business logic interacts with storage abstractions strictly through the generic `CrudRepository<T, ID>` interface, completely separating service layers from physical file Input/Output operations.
4. **Active Business Rule Enforcement**: Fully couples operational enums to live transaction streams. The application actively tracks `MembershipTier` limits ($5,000 to $50,000 metrics) during cash withdrawals, and supports runtime conversions of `OwnershipType` (`PERSONAL` vs. `BUSINESS`) at the administrative terminal layout level.
5. **Declarative Error Management**: Replaces nested conditional tracking blocks with specialized runtime exceptions (`AccountLockedException`, `InsufficientFundsException`), allowing the system to safely interrupt execution and catch errors at clear transaction boundaries.

---

## Version Changelog (Recent Patches)

* **[FIXED]** Corrected `LoanAccount` constructor mapping error by fixing the internal `super()` parameters to supply the required elements (`ownerId`, `startingBalance`) down to the parent abstract class.
* **[FIXED]** Fixed a critical authentication bypass/lockout bug in `LoginSystem` where raw plain text console parameters were compared directly to saved cipher text hashes instead of running input encryption comparisons first.
* **[FEATURE]** Implemented missing master lookup engines in the Compliance Administrative Terminal, allowing system managers to list all registered customers, dynamically modify client membership tiers, and change accounting classifications.

### EncryptionUtils.java
- Replaced insecure string-reversal password "hashing" with proper SHA-256 via MessageDigest
- Replaced string concatenation in loop with StringBuilder for hex encoding

### Customer.java
- Added separate `lastUpdatedAt` field; `getLastUpdatedTimestamp()` was incorrectly returning `lastLogin`
- `suspend()` now cascades and freezes all linked accounts
- `reactivate()` now cascades and unfreezes all linked accounts
- `lastUpdatedAt` is updated on suspend, reactivate, setMembershipTier, and recordLogin

### CreditAccount.java
- Added missing `withdraw()` override; credit limit was never enforced
- Charging now correctly increases the balance (debt) and checks against available credit

### SavingsAccount.java
- Added missing `withdraw()` override; withdrawals were completely unchecked
- Withdrawals now throw InsufficientFundsException if amount exceeds current balance

### TransactionManager.java
- Added customer ownership check to `executeWithdrawal()`
- Added membership tier limit enforcement for withdrawals and transfers
- `executeTransfer()` now takes customerId parameter for security validation

### MenuSystem.java
- Fixed withdrawal call from `TransactionAction.executeWithdrawal()` (invalid static interface call) to `transactionManager.executeWithdrawal()`
- `processMonthlyInterestBatch()` now skips frozen and closed savings accounts to prevent mid-batch exceptions
- Added TransactionManager dependency injection via constructor
- Refactored teller workstation from if-else chain to switch expressions for consistency
- Added option 3 to teller workstation: view all customers and their linked accounts with balance and status
- Fixed `orElseThrow()` calls to include meaningful error messages
- Deposits now persist via `accountRepo.save()` consistent with withdrawal path
- Added transfer option to customer session loop, exposing already-built transfer functionality
- Fixed capitalization inconsistency in menu option labels

---

## Build, Run, and Access

### Compilation
Compile the absolute system package from your terminal workspace root folder:
```bash
javac app/Main.java -d
