package michael.m.marketProject.error;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String existingProperty, String propertyValue) {
        super("User with %s as %s already exists".formatted(propertyValue, existingProperty));
    }
}
