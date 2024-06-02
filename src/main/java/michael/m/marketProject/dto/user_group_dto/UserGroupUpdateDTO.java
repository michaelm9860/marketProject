package michael.m.marketProject.dto.user_group_dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserGroupUpdateDTO {
    @Size(min = 2, max = 32)
    private String name;

    @Size(max = 512)
    private String description;

    private String image;

    private boolean isPrivate;
}
