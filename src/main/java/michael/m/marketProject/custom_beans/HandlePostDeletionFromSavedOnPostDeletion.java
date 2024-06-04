package michael.m.marketProject.custom_beans;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import michael.m.marketProject.entity.User;
import michael.m.marketProject.repository.UserRepository;

import java.util.List;
import java.util.Set;

// This bean is used to handle the deletion of a post from the saved posts of all users who have saved it on post deletion
@RequiredArgsConstructor
public class HandlePostDeletionFromSavedOnPostDeletion {
    private final UserRepository userRepository;

    @Transactional
    public void handleUserSavedPostsOnPostDeletion(Long postId){
        List<User> usersWithPostSaved = userRepository.findUsersBySavedPostsContaining(postId);
        for (User user : usersWithPostSaved) {
            user.getSavedPostsIds().remove(postId);
        }

        userRepository.saveAll(usersWithPostSaved);
    }

    @Transactional
    public void handleUserSavedPostsOnPostDeletion(Set<Long> postIds){
        for (Long postId : postIds) {
            handleUserSavedPostsOnPostDeletion(postId);
        }
    }
}
