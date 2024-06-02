package michael.m.marketProject.error;

public class EntityOwnershipException extends RuntimeException{
    public EntityOwnershipException(String userEmail, String entityType, Long entityId) {
        super("User with email %s does not own %s Entity with id %d".formatted(userEmail, entityType, entityId));
    }
}
