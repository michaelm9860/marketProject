package michael.m.marketProject.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String existingProperty, String propertyValue) {
        super("User with %s as %s already exists".formatted(propertyValue, existingProperty));
    }
}
