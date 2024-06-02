package michael.m.marketProject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import michael.m.marketProject.dto.login.LoginResponseDTO;
import michael.m.marketProject.dto.user_dto.UserCreateDTO;
import michael.m.marketProject.dto.user_dto.UserResponseDTO;
import michael.m.marketProject.service.auth_service.AuthService;
import michael.m.marketProject.dto.login.LoginRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @RequestPart("user") @Valid UserCreateDTO dto,
            @RequestPart("profilePictureFile") MultipartFile profilePictureFile,
            UriComponentsBuilder uriBuilder) {

        return ResponseEntity.created(uriBuilder.path("/login").build().toUri())
                .body(authService.register(dto, profilePictureFile));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        var resDto = authService.login(dto);
        return ResponseEntity.ok(resDto);
    }
}
