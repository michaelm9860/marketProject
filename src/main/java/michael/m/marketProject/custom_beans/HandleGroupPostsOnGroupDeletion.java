package michael.m.marketProject.custom_beans;

import lombok.RequiredArgsConstructor;
import michael.m.marketProject.entity.UserGroup;
import michael.m.marketProject.repository.ProductPostRepository;
import michael.m.marketProject.repository.UserGroupRepository;

@RequiredArgsConstructor
public class HandleGroupPostsOnGroupDeletion {
    private final ProductPostRepository postRepository;

    public void handleGroupPostsOnGroupDeletion(UserGroup group){
        if (!group.getGroupPosts().isEmpty()){
            if (!group.isPrivate()){
                group.getGroupPosts().forEach(post -> post.setGroupId(null));
                postRepository.saveAll(group.getGroupPosts());
            }else {
                postRepository.deleteAll(group.getGroupPosts());
            }
        }
    }
}
