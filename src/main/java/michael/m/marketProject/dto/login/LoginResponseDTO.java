package michael.m.marketProject.dto.login;

import michael.m.marketProject.dto.user_dto.UserResponseDTO;

public record LoginResponseDTO(String jwt, UserResponseDTO user) {
}
