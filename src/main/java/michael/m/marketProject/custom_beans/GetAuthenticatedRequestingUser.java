package michael.m.marketProject.custom_beans;

import lombok.RequiredArgsConstructor;
import michael.m.marketProject.entity.User;
import michael.m.marketProject.error.AuthenticationException;
import michael.m.marketProject.repository.UserRepository;
import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
public class GetAuthenticatedRequestingUser {
    private final UserRepository userRepository;
    public User getRequestingUserEntityByAuthenticationOrThrow(Authentication authentication){
        return userRepository.findUserByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new AuthenticationException("User " + authentication.getName() + " not found")
                );
    }

}
