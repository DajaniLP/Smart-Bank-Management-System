package utils;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return email.contains("@") && email.contains(".") && email.indexOf('@') < email.lastIndexOf('.');
    }
}