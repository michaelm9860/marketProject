package michael.m.marketProject.service.user_service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import michael.m.marketProject.custom_beans.*;
import michael.m.marketProject.dto.product_post_dto.ProductPostResponseDTO;
import michael.m.marketProject.dto.user_dto.UserResponseDTO;
import michael.m.marketProject.dto.user_dto.UserUpdateDTO;
import michael.m.marketProject.entity.ProductPost;
import michael.m.marketProject.entity.User;
import michael.m.marketProject.entity.UserGroup;
import michael.m.marketProject.error.EntityOwnershipException;
import michael.m.marketProject.error.ResourceNotFoundException;
import michael.m.marketProject.repository.ProductPostRepository;
import michael.m.marketProject.repository.UserGroupRepository;
import michael.m.marketProject.repository.UserRepository;
import michael.m.marketProject.service.file_storage_service.FileStorageService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ProductPostRepository productPostRepository;
    private final GetAuthenticatedRequestingUser getAuthenticatedRequestingUser;
    private final AdminRoleChecker adminRoleChecker;
    private final HandlePostDeletionFromSavedOnPostDeletion handlePostDeletionFromSavedOnPostDeletion;
    private final PropertyUpdater propertyUpdater;
    private final UserGroupRepository userGroupRepository;
    private final HandleGroupPostsOnGroupDeletion handleGroupPostsOnGroupDeletion;
    private final FileStorageService fileStorageService;
    private final HandleOldPicturesDeletionOnEntityChange handleOldPicturesDeletionOnEntityChange;

    @PreAuthorize("isAuthenticated()")
    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = getUserEntityOrThrow(id);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public UserResponseDTO getUserByProductPostId(Long postId) {
        ProductPost post = productPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductPost", "id", postId));
        User user = getUserEntityOrThrow(post.getUserId());
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public synchronized UserResponseDTO deleteUserById(Long id, Authentication authentication) {

        User user = getUserEntityOrThrow(id);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
        if (!user.getEmail().equals(authentication.getName()) && !isAdmin) {
            throw new EntityOwnershipException(authentication.getName(), "User", id);
        }
        // Decrement saved count for all posts saved by the user
        Set<Long> savedPostsIds = user.getSavedPostsIds();
        if (!savedPostsIds.isEmpty()) {
            List<ProductPost> savedPosts = productPostRepository.findAllById(savedPostsIds);
            savedPosts.forEach(post -> post.setSavedCount(post.getSavedCount() - 1));
            productPostRepository.saveAll(savedPosts);
        }

        handleUserGroupsOnUserDeletion(user);
        handleOldPicturesDeletionOnEntityChange.deleteOldImage(user.getProfilePicture());

        user.setSavedPostsIds(null);
        deleteAllUserPostsByUserId(id, authentication);
        userRepository.delete(user);
        return modelMapper.map(user, UserResponseDTO.class);

    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public synchronized List<ProductPostResponseDTO> deleteAllUserPostsByUserId(Long userId, Authentication authentication) {
        User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
        boolean isAdmin = adminRoleChecker.isAdmin(authentication);

        if (!user.getId().equals(userId) && !isAdmin) {
            throw new EntityOwnershipException(user.getEmail(), "User", userId);
        }

        List<ProductPost> postsToDelete = productPostRepository.findProductPostsByUserId(userId);

        List<ProductPostResponseDTO> response = postsToDelete.stream()
                .map(p -> modelMapper.map(p, ProductPostResponseDTO.class))
                .toList();

        handlePostDeletionFromSavedOnPostDeletion.handleUserSavedPostsOnPostDeletion(postsToDelete.stream()
                .map(ProductPost::getId)
                .collect(Collectors.toSet()));

        productPostRepository.deleteAll(postsToDelete);

        return response;
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public UserResponseDTO updateUserById(Long id, UserUpdateDTO dto, Authentication authentication, MultipartFile profilePictureFile) {
        User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);

        if (!user.getId().equals(id)) {
            throw new EntityOwnershipException(user.getEmail(), "User", id);
        }

        if (profilePictureFile != null) {
            handleOldPicturesDeletionOnEntityChange.deleteOldImage(user.getProfilePicture());
            String fileName = fileStorageService.storeFile(profilePictureFile);
            dto.setProfilePicture(fileName);
        }

        propertyUpdater.updateNonNullProperties(dto, user);

        var saved = userRepository.save(user);
        return modelMapper.map(saved, UserResponseDTO.class);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public List<UserResponseDTO> getAllUsers(Authentication authentication) {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public List<ProductPostResponseDTO> getAllUserPostsByUserId(Long userId) {
        User user = getUserEntityOrThrow(userId);
        return user.getPosts().stream()
                .map(post -> modelMapper.map(post, ProductPostResponseDTO.class))
                .collect(Collectors.toList());
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public UserResponseDTO sendRequestToJoinGroup(Long groupId, Authentication authentication) {
        User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
        UserGroup userGroup = getUserGroupEntityOrThrow(groupId);
        if (userGroup.isPrivate()){
            userGroup.getPendingMembersIds().add(user.getId());
            user.getGroupsUserIsPendingIn().add(groupId);
        }else {
            userGroup.getGroupMembersIds().add(user.getId());
            user.getGroupIds().add(groupId);
        }
        userGroupRepository.save(userGroup);
        var saved = userRepository.save(user);
        return modelMapper.map(saved, UserResponseDTO.class);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public UserResponseDTO exitGroup(Long groupId, Authentication authentication) {
        User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
        UserGroup userGroup = getUserGroupEntityOrThrow(groupId);
        userGroup.getGroupMembersIds().remove(user.getId());
        user.getGroupIds().remove(groupId);
        if (userGroup.getGroupAdminsIds().contains(user.getId())) {
            userGroup.getGroupAdminsIds().remove(user.getId());
            user.getGroupsUserIsAdminOf().remove(groupId);
        }
        if (userGroup.getGroupMembersIds().isEmpty()){
            handleGroupPostsOnGroupDeletion.handleGroupPostsOnGroupDeletion(userGroup);
            handleOldPicturesDeletionOnEntityChange.deleteOldImage(userGroup.getImage());
            userGroupRepository.delete(userGroup);
        }else {
            userGroupRepository.save(userGroup);
        }
        var saved = userRepository.save(user);
        return modelMapper.map(saved, UserResponseDTO.class);
    }


    private User getUserEntityOrThrow(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private UserGroup getUserGroupEntityOrThrow(Long id) {
        return userGroupRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("UserGroup", "id", id)
        );
    }


    private void handleUserGroupsOnUserDeletion(User user) {
        Set<Long> groupIds = user.getGroupIds();
        if (!groupIds.isEmpty()) {
            List<UserGroup> groups = userGroupRepository.findAllById(groupIds);
            List<UserGroup> emptyGroupsToDelete = new ArrayList<>();
            groups.forEach(group -> {
                group.getGroupMembersIds().remove(user.getId());
                group.getGroupAdminsIds().remove(user.getId());
                group.getPendingMembersIds().remove(user.getId());
                if(group.getGroupMembersIds().isEmpty()){
                    emptyGroupsToDelete.add(group);
                }
            });
            userGroupRepository.deleteAll(emptyGroupsToDelete);
            userGroupRepository.saveAll(groups);
        }
        user.setGroupIds(null);
        user.setGroupsUserIsAdminOf(null);
        user.setGroupsUserIsPendingIn(null);
    }
}
