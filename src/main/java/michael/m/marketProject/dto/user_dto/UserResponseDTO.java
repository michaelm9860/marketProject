package michael.m.marketProject.dto.user_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import michael.m.marketProject.dto.product_post_dto.ProductPostResponseDTO;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;
    private String phoneNumber;
    private String location;
    private LocalDateTime createdAt;
    private Set<ProductPostResponseDTO> posts;
    private Set<Long> savedPostsIds;
    private Set<Long> groupIds;
    private Set<Long> groupsUserIsAdminOf;
    private Set<Long> groupsUserIsPendingIn;
}
