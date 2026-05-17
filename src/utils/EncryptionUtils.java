package utils;

public class EncryptionUtils {

    public static String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        
        String reversed = "";
        for (int i = password.length() - 1; i >= 0; i--) {
            reversed += password.charAt(i);
        }
        
        return "SECURE_" + reversed;
    }
}
