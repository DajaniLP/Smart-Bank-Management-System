package exceptions;

public class CustomerNotFoundException extends BankException {
    public CustomerNotFoundException (String message) {
        super(message);
    }

}
