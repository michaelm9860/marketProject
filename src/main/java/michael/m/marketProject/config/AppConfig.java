package michael.m.marketProject.config;

import lombok.RequiredArgsConstructor;
import michael.m.marketProject.custom_beans.*;
import michael.m.marketProject.repository.ProductPostRepository;
import michael.m.marketProject.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepository userRepository;
    private final ProductPostRepository postRepository;
    @Bean
    ModelMapper getModelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AdminRoleChecker isUserAdmin(){
        return AdminRoleChecker.adminRoleChecker();
    }

    @Bean
    public GetAuthenticatedRequestingUser getAuthenticatedRequestingUser(){
        return new GetAuthenticatedRequestingUser(userRepository);
    }

    @Bean
    public HandlePostDeletionFromSavedOnPostDeletion handlePostDeletionFromSavedOnPostDeletion(){
        return new HandlePostDeletionFromSavedOnPostDeletion(userRepository);
    }

    @Bean
    public HandleGroupPostsOnGroupDeletion handleGroupPostsOnGroupDeletion(){
        return new HandleGroupPostsOnGroupDeletion(postRepository);
    }

    @Bean
    public PropertyUpdater propertyUpdater(){
        return new PropertyUpdater();
    }
}