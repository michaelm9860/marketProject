package michael.m.marketProject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import michael.m.marketProject.dto.user_group_dto.UserGroupCreateDTO;
import michael.m.marketProject.dto.user_group_dto.UserGroupListDTO;
import michael.m.marketProject.dto.user_group_dto.UserGroupResponseDTO;
import michael.m.marketProject.dto.user_group_dto.UserGroupUpdateDTO;
import michael.m.marketProject.service.user_group_service.UserGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class UserGroupController {
    private final UserGroupService userGroupService;

    @PostMapping
    public ResponseEntity<UserGroupResponseDTO> createUserGroup(
            Authentication authentication,
            @RequestPart("group") @Valid UserGroupCreateDTO dto,
            @RequestPart("imageFile") MultipartFile imageFile,
            UriComponentsBuilder uriBuilder){

        var res = userGroupService.createUserGroup(dto, imageFile, authentication);

        var uri = uriBuilder.path("/api/v1/groups/{id}").buildAndExpand(res.getId()).toUri();
        return ResponseEntity.created(uri).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGroupResponseDTO> getUserGroupById(@PathVariable Long id, Authentication authentication){
        return ResponseEntity.ok(userGroupService.getUserGroupById(id, authentication));
    }

    @GetMapping
    public ResponseEntity<UserGroupListDTO> getAllUserGroups(
            Authentication authentication,
            @RequestParam(value = "pageNum", required = false, defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String... sortBy){
        return ResponseEntity.ok(userGroupService.getAllUserGroups(authentication, pageNum, pageSize, sortDir, sortBy));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserGroupResponseDTO> updateUserGroupById(
            Authentication authentication,
            @PathVariable Long id,
            @RequestPart("updateDetails") @Valid UserGroupUpdateDTO dto,
            @RequestPart(name = "imageFile", required = false) MultipartFile imageFile){
        return ResponseEntity.ok(userGroupService.updateUserGroupById(id, dto, authentication, imageFile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserGroupResponseDTO> deleteUserGroupById(@PathVariable Long id){
        return ResponseEntity.ok(userGroupService.deleteUserGroupById(id));
    }

    @PutMapping("/{groupId}/users/{userId}")
    public ResponseEntity<UserGroupResponseDTO> addOrRemoveUserFromGroup(
            Authentication authentication,
            @PathVariable Long groupId,
            @PathVariable Long userId){
        return ResponseEntity.ok(userGroupService.addOrRemoveUserFromGroup(groupId, userId, authentication));
    }

    @PutMapping("/{groupId}/users/{userId}/approve")
    public ResponseEntity<UserGroupResponseDTO> approvePendingMember(
            Authentication authentication,
            @PathVariable Long groupId,
            @PathVariable Long userId){
        return ResponseEntity.ok(userGroupService.approvePendingMember(groupId, userId, authentication));
    }

    @PutMapping("/{groupId}/users/{userId}/reject")
    public ResponseEntity<UserGroupResponseDTO> rejectPendingMember(
            Authentication authentication,
            @PathVariable Long groupId,
            @PathVariable Long userId){
        return ResponseEntity.ok(userGroupService.rejectPendingMember(groupId, userId, authentication));
    }

    @PutMapping("/{groupId}/users/{userId}/promote")
    public ResponseEntity<UserGroupResponseDTO> promoteToAdmin(
            Authentication authentication,
            @PathVariable Long groupId,
            @PathVariable Long userId){
        return ResponseEntity.ok(userGroupService.promoteToAdmin(groupId, userId, authentication));
    }
}
