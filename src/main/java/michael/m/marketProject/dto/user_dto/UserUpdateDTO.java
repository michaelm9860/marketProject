package michael.m.marketProject.dto.user_dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserUpdateDTO {

    @Size(min = 2, max = 24)
    private String firstName;

    @Size(min = 2, max = 24)
    private String lastName;

    private String profilePicture;

    @Size(min = 2, max = 32)
    private String location;
}
