package michael.m.marketProject.service.user_group_service;

import michael.m.marketProject.dto.user_group_dto.UserGroupCreateDTO;
import michael.m.marketProject.dto.user_group_dto.UserGroupListDTO;
import michael.m.marketProject.dto.user_group_dto.UserGroupResponseDTO;
import michael.m.marketProject.dto.user_group_dto.UserGroupUpdateDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserGroupService {
    UserGroupResponseDTO createUserGroup(UserGroupCreateDTO dto, Authentication authentication);

    UserGroupResponseDTO getUserGroupById(Long id, Authentication authentication);

    UserGroupListDTO getAllUserGroups(Authentication authentication,int pageNum, int pageSize, String sortDir, String... sortBy);

    UserGroupResponseDTO updateUserGroupById(Long id, UserGroupUpdateDTO dto, Authentication authentication);

    UserGroupResponseDTO deleteUserGroupById(Long id);

    UserGroupResponseDTO addOrRemoveUserFromGroup(Long groupId, Long userId, Authentication authentication);

    UserGroupResponseDTO approvePendingMember(Long groupId, Long userId, Authentication authentication);

    UserGroupResponseDTO rejectPendingMember(Long groupId, Long userId, Authentication authentication);

    UserGroupResponseDTO promoteToAdmin(Long groupId, Long userId, Authentication authentication);
}

