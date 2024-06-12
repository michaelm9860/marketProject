package michael.m.marketProject.error;

public class GroupMembershipException extends RuntimeException{
    public GroupMembershipException(String userEmail, Long groupId){
        super("User with email %s is not a member of the group with id %d".formatted(userEmail, groupId));
    }
    public GroupMembershipException(String message){
        super(message);
    }
}
