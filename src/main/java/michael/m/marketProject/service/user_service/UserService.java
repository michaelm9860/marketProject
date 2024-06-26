package michael.m.marketProject.service.user_service;

import michael.m.marketProject.dto.product_post_dto.ProductPostResponseDTO;
import michael.m.marketProject.dto.user_dto.UserResponseDTO;
import michael.m.marketProject.dto.user_dto.UserUpdateDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    UserResponseDTO getUserById(Long id);

    UserResponseDTO getUserByProductPostId(Long postId);

    UserResponseDTO deleteUserById(Long id, Authentication authentication);

    List<ProductPostResponseDTO> deleteAllUserPostsByUserId(Long userId, Authentication authentication);

    UserResponseDTO updateUserById(Long id, UserUpdateDTO dto, Authentication authentication, MultipartFile profilePictureFile);

    List<UserResponseDTO> getAllUsers(Authentication authentication);

    UserResponseDTO sendRequestToJoinGroup(Long groupId, Authentication authentication);

    UserResponseDTO exitGroup(Long groupId, Authentication authentication);
}
