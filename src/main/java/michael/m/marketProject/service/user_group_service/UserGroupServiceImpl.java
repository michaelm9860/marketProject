package michael.m.marketProject.service.user_group_service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import michael.m.marketProject.custom_beans.*;
import michael.m.marketProject.dto.user_group_dto.UserGroupCreateDTO;
import michael.m.marketProject.dto.user_group_dto.UserGroupListDTO;
import michael.m.marketProject.dto.user_group_dto.UserGroupResponseDTO;
import michael.m.marketProject.dto.user_group_dto.UserGroupUpdateDTO;
import michael.m.marketProject.entity.User;
import michael.m.marketProject.entity.UserGroup;
import michael.m.marketProject.error.GroupAdminPrivilegesException;
import michael.m.marketProject.error.PaginationException;
import michael.m.marketProject.error.ResourceNotFoundException;
import michael.m.marketProject.repository.UserGroupRepository;
import michael.m.marketProject.repository.UserRepository;
import michael.m.marketProject.service.file_storage_service.FileStorageService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserGroupServiceImpl implements UserGroupService {

    private final UserGroupRepository userGroupRepository;
    private final GetAuthenticatedRequestingUser getAuthenticatedRequestingUser;
    private final ModelMapper modelMapper;
    private final PropertyUpdater propertyUpdater;
    private final UserRepository userRepository;
    private final AdminRoleChecker adminRoleChecker;
    private final HandleGroupPostsOnGroupDeletion handleGroupPostsOnGroupDeletion;
    private final FileStorageService fileStorageService;
    private final HandleOldPicturesDeletionOnEntityChange handleOldPicturesDeletionOnEntityChange;

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public UserGroupResponseDTO createUserGroup(UserGroupCreateDTO dto, MultipartFile imageFile, Authentication authentication) {
        User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);

        String fileName = fileStorageService.storeFile(imageFile);
        dto.setImage(fileName);

        UserGroup userGroup = modelMapper.map(dto, UserGroup.class);

        userGroup.getGroupAdminsIds().add(user.getId());
        userGroup.getGroupMembersIds().add(user.getId());
        var saved = userGroupRepository.save(userGroup);

        user.getGroupsUserIsAdminOf().add(userGroup.getId());
        user.getGroupIds().add(userGroup.getId());

        userRepository.save(user);

        return modelMapper.map(saved, UserGroupResponseDTO.class);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public UserGroupResponseDTO getUserGroupById(Long id, Authentication authentication) {
        UserGroup userGroup = getUserGroupEntityOrThrow(id);
        if (userGroup.isPrivate()){
            User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
            if (!userGroup.getGroupMembersIds().contains(user.getId()) && !adminRoleChecker.isAdmin(authentication)){
                userGroup.setGroupPosts(null);
            }
        }
        return modelMapper.map(userGroup, UserGroupResponseDTO.class);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public UserGroupListDTO getAllUserGroups(Authentication authentication, int pageNum, int pageSize, String sortDir, String... sortBy) {
        try {
            Sort.Direction sort = Sort.Direction.fromString(sortDir);
            var pageable = PageRequest.of(pageNum, pageSize, sort, sortBy);

            Page<UserGroup> pr = userGroupRepository.findAll(pageable);

            if (pageNum > pr.getTotalPages()) {
                throw new PaginationException("Page Number " + pageNum + " Exceeds totalPages " + pr.getTotalPages());
            }

            User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
            Long userId = user.getId();

            List<UserGroupResponseDTO> groupListDto = pr.getContent().stream()
                    .map(g -> {
                        UserGroupResponseDTO dto = modelMapper.map(g, UserGroupResponseDTO.class);
                        if (dto.isPrivate() && !dto.getGroupMembersIds().contains(userId)) {
                            dto.setGroupPosts(null);
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

            return new UserGroupListDTO(
                    pr.getTotalElements(),
                    pr.getNumber(),
                    pr.getSize(),
                    pr.getTotalPages(),
                    pr.isFirst(),
                    pr.isLast(),
                    groupListDto
            );
        } catch (IllegalArgumentException e) {
            throw new PaginationException(e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public UserGroupResponseDTO updateUserGroupById(Long id, UserGroupUpdateDTO dto, Authentication authentication, MultipartFile imageFile) {
        User user = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
        UserGroup userGroup = getUserGroupEntityOrThrow(id);
        userIsAdminOfGroup(user, userGroup);

        if (imageFile != null) {
            handleOldPicturesDeletionOnEntityChange.deleteOldImage(userGroup.getImage());
            String fileName = fileStorageService.storeFile(imageFile);
            dto.setImage(fileName);
        }

        propertyUpdater.updateNonNullProperties(dto, userGroup);

        if (userGroup.isPrivate() && !userGroup.getPendingMembersIds().isEmpty() && !dto.isPrivate()) {
            for (Long pendingMemberId : userGroup.getPendingMembersIds()) {
                User pendingMember = getUserEntityOrThrow(pendingMemberId);
                pendingMember.getGroupsUserIsPendingIn().remove(userGroup.getId());
                pendingMember.getGroupIds().add(userGroup.getId());
                userRepository.save(pendingMember);
            }
            userGroup.getPendingMembersIds().clear();
        }

        userGroup.setPrivate(dto.isPrivate());

        var saved = userGroupRepository.save(userGroup);

        return modelMapper.map(saved, UserGroupResponseDTO.class);
    }

    //Only the global admin can delete a user group, otherwise the group will only be deleted if all members leave.
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @Override
    public UserGroupResponseDTO deleteUserGroupById(Long id) {
        UserGroup userGroup = getUserGroupEntityOrThrow(id);
        handleGroupPostsOnGroupDeletion.handleGroupPostsOnGroupDeletion(userGroup);
        handleOldPicturesDeletionOnEntityChange.deleteOldImage(userGroup.getImage());
        userGroupRepository.delete(userGroup);
        return modelMapper.map(userGroup, UserGroupResponseDTO.class);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public UserGroupResponseDTO addOrRemoveUserFromGroup(Long groupId, Long userId, Authentication authentication) {
        User requestingUser = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
        UserGroup userGroup = getUserGroupEntityOrThrow(groupId);
        User userToBeAddedOrRemoved = getUserEntityOrThrow(userId);

        userIsAdminOfGroup(requestingUser, userGroup);

        if (userGroup.getGroupMembersIds().contains(userId)) {
            userGroup.getGroupMembersIds().remove(userId);
            userToBeAddedOrRemoved.getGroupIds().remove(groupId);
            if (userGroup.getGroupAdminsIds().contains(userId)) {
                userGroup.getGroupAdminsIds().remove(userId);
                userToBeAddedOrRemoved.getGroupsUserIsAdminOf().remove(groupId);
            }
        } else {
            userGroup.getGroupMembersIds().add(userId);
            userToBeAddedOrRemoved.getGroupIds().add(groupId);
            userGroup.getPendingMembersIds().remove(userId);
        }

        userRepository.save(userToBeAddedOrRemoved);

        var saved = userGroupRepository.save(userGroup);

        return modelMapper.map(saved, UserGroupResponseDTO.class);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public UserGroupResponseDTO approvePendingMember(Long groupId, Long userId, Authentication authentication) {
        User requestingUser = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
        UserGroup userGroup = getUserGroupEntityOrThrow(groupId);
        userIsAdminOfGroup(requestingUser, userGroup);
        User userToBeAdded = getUserEntityOrThrow(userId);
        if (!userGroup.getPendingMembersIds().contains(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        userGroup.getPendingMembersIds().remove(userId);
        userGroup.getGroupMembersIds().add(userId);
        userToBeAdded.getGroupIds().add(groupId);
        userToBeAdded.getGroupsUserIsPendingIn().remove(groupId);
        userRepository.save(userToBeAdded);
        var saved = userGroupRepository.save(userGroup);
        return modelMapper.map(saved, UserGroupResponseDTO.class);

    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public UserGroupResponseDTO rejectPendingMember(Long groupId, Long userId, Authentication authentication) {
        User requestingUser = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
        UserGroup userGroup = getUserGroupEntityOrThrow(groupId);
        userIsAdminOfGroup(requestingUser, userGroup);
        User userToBeRejected = getUserEntityOrThrow(userId);
        if (!userGroup.getPendingMembersIds().contains(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        userGroup.getPendingMembersIds().remove(userId);
        userToBeRejected.getGroupsUserIsPendingIn().remove(groupId);

        userRepository.save(userToBeRejected);
        var saved = userGroupRepository.save(userGroup);
        return modelMapper.map(saved, UserGroupResponseDTO.class);
    }


    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Override
    public UserGroupResponseDTO promoteToAdmin(Long groupId, Long userId, Authentication authentication) {
        User requestingUser = getAuthenticatedRequestingUser.getRequestingUserEntityByAuthenticationOrThrow(authentication);
        UserGroup userGroup = getUserGroupEntityOrThrow(groupId);
        userIsAdminOfGroup(requestingUser, userGroup);
        User userToBePromoted = getUserEntityOrThrow(userId);
        userGroup.getGroupAdminsIds().add(userId);
        userToBePromoted.getGroupsUserIsAdminOf().add(groupId);
        userRepository.save(userToBePromoted);
        var saved = userGroupRepository.save(userGroup);
        return modelMapper.map(saved, UserGroupResponseDTO.class);
    }


    private UserGroup getUserGroupEntityOrThrow(Long id) {
        return userGroupRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("UserGroup", "id", id)
        );
    }

    private void userIsAdminOfGroup(User user, UserGroup userGroup) {
        if (!userGroup.getGroupAdminsIds().contains(user.getId())) {
            throw new GroupAdminPrivilegesException(user.getEmail(), userGroup.getId());
        }
    }

    private User getUserEntityOrThrow(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

}
