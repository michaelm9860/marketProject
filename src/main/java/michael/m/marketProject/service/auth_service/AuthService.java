package michael.m.marketProject.service.auth_service;
import michael.m.marketProject.dto.login.LoginResponseDTO;
import michael.m.marketProject.dto.user_dto.UserCreateDTO;
import michael.m.marketProject.dto.user_dto.UserResponseDTO;
import michael.m.marketProject.dto.login.LoginRequestDTO;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    UserResponseDTO register(UserCreateDTO dto);

    LoginResponseDTO login(LoginRequestDTO dto);
}
