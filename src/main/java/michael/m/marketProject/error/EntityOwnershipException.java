package michael.m.marketProject.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class EntityOwnershipException extends RuntimeException{
    public EntityOwnershipException(String userEmail, String entityType, Long entityId) {
        super("User with email %s does not own %s Entity with id %d".formatted(userEmail, entityType, entityId));
    }
}
