// Module 5: Data Storage & Exception Handling
public class Validation {
    public static void validateNotEmpty(String value, String fieldName) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty");
        }
    }

    public static void validateAge(int age) throws InvalidInputException {
        if (age < 0 || age > 120) {
            throw new InvalidInputException("Invalid age provided");
        }
    }

    public static void validatePhone(String phone) throws InvalidInputException {
        if (phone == null || !phone.matches("\\\\d{10}")) {
            throw new InvalidInputException("Phone number must be exactly 10 digits");
        }
    }
}
