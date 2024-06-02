package michael.m.marketProject.repository;

import michael.m.marketProject.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByNameIgnoreCase(String roleName);
}
