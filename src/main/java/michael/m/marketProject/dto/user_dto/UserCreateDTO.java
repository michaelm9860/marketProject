package michael.m.marketProject.dto.user_dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class UserCreateDTO {
    @NotNull
    @Size(min = 2, max = 24)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 24)
    private String lastName;

    private String profilePicture;

    @NotNull
    @Pattern(regexp = "^\\+\\d{1,15}$",
            message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull
    @Email
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email format"
    )
    private String email;

    @NotNull
    @Size(min = 2, max = 32)
    private String location;

    @NotNull
    @Size(min = 8, max = 20, message = "Password should have between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$", message = "password must contain at least 1 lowercase letter, 1 uppercase letter, 1 digit and 1 special character")
    private String password;

}

