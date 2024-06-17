package michael.m.marketProject.dto.user_group_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroupListDTO {
    private long totalGroups;
    private int pageNum;
    private int pageSize;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private Collection<UserGroupResponseDTO> userGroups;
}
