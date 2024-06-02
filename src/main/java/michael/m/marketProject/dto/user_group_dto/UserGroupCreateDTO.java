package michael.m.marketProject.dto.user_group_dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserGroupCreateDTO {
    @NotNull
    @Size(min = 2, max = 32)
    private String name;

    @NotNull
    @Size(max = 512)
    private String description;

    private String image;

    private boolean isPrivate;
}
