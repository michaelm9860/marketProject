package michael.m.marketProject.service.auth_service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import michael.m.marketProject.dto.login.LoginRequestDTO;
import michael.m.marketProject.dto.login.LoginResponseDTO;
import michael.m.marketProject.error.AuthenticationException;
import michael.m.marketProject.error.ResourceNotFoundException;
import michael.m.marketProject.service.file_storage_service.FileStorageService;
import michael.m.marketProject.service.jwt_service.JWTService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import michael.m.marketProject.dto.user_dto.UserCreateDTO;
import michael.m.marketProject.dto.user_dto.UserResponseDTO;
import michael.m.marketProject.entity.Role;
import michael.m.marketProject.entity.User;
import michael.m.marketProject.error.UserAlreadyExistsException;
import michael.m.marketProject.repository.RoleRepository;
import michael.m.marketProject.repository.UserRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final JWTService jwtService;

    private final FileStorageService fileStorageService;

    @Transactional
    @Override
    public UserResponseDTO register(UserCreateDTO dto, MultipartFile profilePictureFile) {
        checkIfEmailExists(dto);
        checkIfPhoneNumberExists(dto);


        String fileName = fileStorageService.storeFile(profilePictureFile);
        dto.setProfilePicture(fileName);

        User user = modelMapper.map(dto, User.class);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        Role role = roleRepository.findRoleByNameIgnoreCase("ROLE_USER").orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(role));

        var saved = userRepository.save(user);

        return modelMapper.map(saved, UserResponseDTO.class);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        var user = userRepository.findUserByEmailIgnoreCase(dto.email()).orElseThrow(
                () -> new AuthenticationException("Username or password don't match")
        );

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new AuthenticationException("Username or password don't match");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                dto.password(),
                user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).toList()
        );

        var userResponseDTO = modelMapper.map(user, UserResponseDTO.class);

        var jwt = jwtService.jwtToken(authentication);
        return new LoginResponseDTO(jwt, userResponseDTO);
    }

    private void checkIfEmailExists(UserCreateDTO dto) {
        if (userRepository.findUserByEmailIgnoreCase(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("email", dto.getEmail());
        }
    }

    private void checkIfPhoneNumberExists(UserCreateDTO dto) {
        if (userRepository.findUserByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new UserAlreadyExistsException("phone number", dto.getPhoneNumber());
        }
    }

    // Login will be preformed through email and password instead of username and password since email is unique. Therefore, we will use email as the username.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserEntityOrThrow(email);

        var roles = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), roles);
    }

    private User getUserEntityOrThrow(String email) {
        return userRepository.findUserByEmailIgnoreCase(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email)
        );
    }
}
