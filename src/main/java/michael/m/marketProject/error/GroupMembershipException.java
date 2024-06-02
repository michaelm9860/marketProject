package michael.m.marketProject.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class GroupMembershipException extends RuntimeException{
    public GroupMembershipException(String userEmail, Long groupId){
        super("User with email %s is not a member of the group with id %d".formatted(userEmail, groupId));
    }
}
