package models.accounts;

import enums.AccountType;

public class SavingsAccount extends BankAccount {
    private static final long serialVersionUID = 1L;

    private double interestRate; // e.g., 0.04 for 4%

    public SavingsAccount(String ownerId, double startingBalance, double interestRate) {
        super(ownerId, startingBalance);
        this.interestRate = interestRate;
    }

    @Override 
    public AccountType getType() {
        return AccountType.SAVINGS;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public void applyEndOfMonthInterest() {
        validateAccountActive();
        double calculatedInterest = getBalance() * (interestRate / 12.0);
        setBalance(getBalance() + calculatedInterest);
        recordTransaction("Accrued monthly compound interest: +" + String.format("$%.2f", calculatedInterest));
    }
}