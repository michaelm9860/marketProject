package michael.m.marketProject.config;

import lombok.RequiredArgsConstructor;

import michael.m.marketProject.entity.Role;
import michael.m.marketProject.entity.User;
import michael.m.marketProject.repository.RoleRepository;
import michael.m.marketProject.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class SQLRunner implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            var adminRole = roleRepository.save(new Role(1L, "ROLE_ADMIN"));
            var userRole = roleRepository.save(new Role(2L, "ROLE_USER"));

            userRepository.save(
                    User.builder()
                            .id(1L)
                            .firstName("admin")
                            .lastName("admin")
                            .phoneNumber("+0000000000")
                            .profilePicture("admin_pfp.png")
                            .location("Earth")
                            .email("admin1@admin.com")
                            .password(passwordEncoder.encode("Passw0rd1!"))
                            .roles(Set.of(adminRole))
                            .posts(new HashSet<>())
                            .savedPostsIds(new HashSet<>())
                            .build()
            );

            userRepository.save(
                    User.builder()
                            .id(2L)
                            .firstName("user")
                            .lastName("user")
                            .phoneNumber("+11111111111")
                            .profilePicture("1111111111")
                            .location("Earth")
                            .email("user1@user.com")
                            .password(passwordEncoder.encode("Passw0rd1!"))
                            .roles(Set.of(userRole))
                            .posts(new HashSet<>())
                            .savedPostsIds(new HashSet<>())
                            .build()
            );
        }
    }
}
//TODO: update initial seedings data to valid data