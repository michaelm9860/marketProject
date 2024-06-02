package michael.m.marketProject.error;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entityName, String property, Object value) {
        super("Entity %s with %s=%s not found".formatted(entityName, property, value));
    }
}
