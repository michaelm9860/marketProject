package michael.m.marketProject.service.auth_service;
import michael.m.marketProject.dto.login.LoginResponseDTO;
import michael.m.marketProject.dto.user_dto.UserCreateDTO;
import michael.m.marketProject.dto.user_dto.UserResponseDTO;
import michael.m.marketProject.dto.login.LoginRequestDTO;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService extends UserDetailsService {
    UserResponseDTO register(UserCreateDTO dto, MultipartFile profilePictureFile);

    LoginResponseDTO login(LoginRequestDTO dto);
}
