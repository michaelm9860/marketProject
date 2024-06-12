package michael.m.marketProject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import michael.m.marketProject.dto.product_post_dto.ProductPostResponseDTO;
import michael.m.marketProject.dto.user_dto.UserResponseDTO;
import michael.m.marketProject.dto.user_dto.UserUpdateDTO;
import michael.m.marketProject.service.user_service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(Authentication authentication) {
        return ResponseEntity.ok(userService.getAllUsers(authentication));
    }

    @GetMapping(params = "postId")
    public ResponseEntity<UserResponseDTO> getUserByProductPostId(@RequestParam Long postId) {
        return ResponseEntity.ok(userService.getUserByProductPostId(postId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUserById(
            @PathVariable Long id,
            @RequestPart("updateDetails") @Valid UserUpdateDTO dto,
            @RequestPart(name = "profilePictureFile", required = false) MultipartFile profilePictureFile,
            Authentication authentication) {
        return ResponseEntity.ok(userService.updateUserById(id, dto, authentication, profilePictureFile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDTO> deleteUserById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(userService.deleteUserById(id, authentication));
    }

    @DeleteMapping("/{userId}/posts")
    public List<ProductPostResponseDTO> deleteAllUserPostsByUserId(@PathVariable Long userId, Authentication authentication) {
        return userService.deleteAllUserPostsByUserId(userId, authentication);
    }

    @PutMapping("{groupId}/sendJoinRequest")
    public ResponseEntity<UserResponseDTO> sendRequestToJoinGroup(@PathVariable Long groupId, Authentication authentication) {
        return ResponseEntity.ok(userService.sendRequestToJoinGroup(groupId, authentication));
    }

    @PutMapping("{groupId}/exitGroup")
    public ResponseEntity<UserResponseDTO> exitGroup(@PathVariable Long groupId, Authentication authentication) {
        return ResponseEntity.ok(userService.exitGroup(groupId, authentication));
    }
}
