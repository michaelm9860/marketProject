package michael.m.marketProject.custom_beans;

import org.springframework.security.core.Authentication;

@FunctionalInterface
public interface AdminRoleChecker {
    boolean isAdmin(Authentication authentication);

    static AdminRoleChecker adminRoleChecker() {
        return authentication -> authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
