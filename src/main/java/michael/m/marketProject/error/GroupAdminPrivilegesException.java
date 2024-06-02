package michael.m.marketProject.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class GroupAdminPrivilegesException extends RuntimeException{
    public GroupAdminPrivilegesException(String userEmail, Long entityId) {
        super("User with email %s is not an admin of group with id %d".formatted(userEmail, entityId));
    }
}