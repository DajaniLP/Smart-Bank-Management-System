(Finished! V1)

# Smart Bank Management System

The Smart Bank Management System is a production-grade, terminal-based enterprise console application written in Java. The platform relies on a clean layered design and core object-oriented principles to govern secure multi-user portal entries, perform transactional constraint validations across structural financial asset tiers, and preserve entity states across reboots using persistent binary object stream serialization.

---

## Technical Architecture & Design Layers

The application code is separated into dedicated domain packages to minimize class dependencies and isolate structural concerns:

* **`app` (Application Bootstrap)**
  * `Main.java`: The core application entry point. Instantiates binary files, establishes persistence repositories, injects layer dependencies, and boots up the user interface routing threads.

* **`enums` (Operational Invariants)**
  * `AccountStatus.java`: Tracks administrative flags for financial resources (`ACTIVE`, `LOCKED`, `FROZEN`, `CLOSED`).
  * `AccountType.java`: Groups specialized accounting options (`CHECKING`, `SAVINGS`, `CREDIT`, `LOAN`).
  * `CustomerStatus.java`: Flags profile usage rights (`ACTIVE`, `SUSPENDED`, `CLOSED`).
  * `MembershipTier.java`: Holds account validation parameters matching clients to daily financial activity rules (`REGULAR`, `PREMIUM`, `VIP`).
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
  * `AccountManager.java`: Verifies configuration constraints when establishing checking, savings, loan, or credit portfolios.
  * `TransactionManager.java`: Handles cross-account balance adjustments, processing funds movements while logging ledger activity.

* **`models` (Structural Entities)**
  * `models/people/Person.java`: Abstract root class housing core client profiles, demographic strings, and physical identification metrics.
  * `models/people/Customer.java`: Specialized class extending `Person` that tracks profile metadata, security fields, and links to personal portfolios.
  * `models/accounts/BankAccount.java`: Abstract base entity tracking unique routing keys, status flags, timestamps, and history streams.
  * `models/accounts/CheckingAccount.java`: Implements structural adjustments utilizing custom overdraft protection limits.
  * `models/accounts/SavingsAccount.java`: Computes periodic yield adjustments based on interest rates.
  * `models/accounts/CreditAccount.java`: Tracks outstanding credit usage against a fixed debt limit.
  * `models/accounts/LoanAccount.java`: Collects static liabilities and locks customer access to prevent direct manual withdrawals.

* **`repositories` (Data Persistence Layer)**
  * `CustomerRepository.java`: Interacts directly with `customers.dat` via object stream serialization. Features self-healing capabilities to reconstruct corrupt file headers.
  * `BankAccountRepository.java`: Interacts directly with `accounts.dat` via object stream serialization to load and persist financial asset states.

* **`systems` (Console Routing Subsystems)**
  * `LoginSystem.java`: Manages authentication loops, tests passwords against user security records, and blocks locked profiles.
  * `MenuFormatter.java`: Provides terminal layout utilities, including aligned display boxes and console progress metrics.
  * `MenuSystem.java`: Controls application routing, providing isolated submenus for customers, tellers, and administrative tasks.

* **`utils` (System Support Helpers)**
  * `EncryptionUtils.java`: Implements lightweight string adjustments to protect stored credentials.
  * `ValidationUtils.java`: Evaluates user inputs against specific layout rules, including standard email string validation.

---

## Technical Highlights & OOP Strategy

1. **Polymorphic Invariants**: Common actions rely on shared interface signatures (`TransactionAction`). Subclasses override these methods to enforce specific behaviors, such as evaluating overdraft buffers on checking accounts or preventing manual withdrawals on loans.
2. **Decoupled Data Architecture**: Business logic interact with storage abstractions strictly through the generic `CrudRepository<T, ID>` interface. This decouples service layers from physical file operations.
3. **Automated State Storage**: System states are serialized directly to disk as binary object data (`.dat`). File repositories handle stream reading, data synchronization, and error collection during startup routines.
4. **Declarative Error Management**: Replaces nested conditional tracking blocks with specialized runtime exceptions, allowing the system to catch and handle logic errors at explicit transaction boundaries.

---

## Project Backlog & Planned Implementation

The following structural components and logic extensions are scheduled for integration during upcoming updates:

1. **LoanAccount Constructor Routing Revision**
   * *Status*: Missing / Broken Dependency.
   * *Action*: Update the initialization paths within `LoanAccount.java`. The constructor must be updated to pass both the customer assignment strings and the starting principal debt balance directly to the abstract `BankAccount` base class (`super(ownerId, startingBalance)`), fixing compilation conflicts with parent models.

2. **Transaction-Level Membership Limit Enforcement**
   * *Status*: Incomplete Domain Logic.
   * *Action*: Update the transaction tracking code inside the service managers to reference configuration traits stored within the `MembershipTier` options (`REGULAR`, `PREMIUM`, `VIP`). Validations must cross-check withdrawal and transfer requests against daily limits to ensure compliance.

3. **Global Analytics & Descriptive Statistical Search**
   * *Status*: Missing Analytics Module.
   * *Action*: Implement an aggregation utility inside the repository layers. The engine must compile structural business metrics, including total system liquidity, average client balance profiles, total asset allocations, outstanding debt counts, and match specific target string records across file databases.

4. **Console Interaction Optimization**
   * *Status*: UI Refinement.
   * *Action*: Simplify navigation pathways within the main loops. This includes removing redundant menu choices, streamlining data entry steps, and applying input filters to prevent terminal routing crashes.

---

## Build, Run, and Access

### Compilation
Compile the absolute system package from your terminal workspace root folder:
```bash
javac app/Main.java -d out
