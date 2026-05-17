package exceptions;

public class AccountLockedException extends BankException {
    public AccountLockedException (String message) {
        super(message);
    }

}
