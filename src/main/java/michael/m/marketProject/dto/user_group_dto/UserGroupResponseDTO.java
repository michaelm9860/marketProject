package michael.m.marketProject.dto.user_group_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import michael.m.marketProject.entity.ProductPost;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserGroupResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Set<Long> groupMembersIds;
    private Set<Long> groupAdminsIds;
    private Set<Long> pendingMembersIds;
    private String image;
    private Set<ProductPost> groupPosts;
    private boolean isPrivate;
    private LocalDateTime createdAt;

}
