package michael.m.marketProject.repository;


import michael.m.marketProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmailIgnoreCase(String email);

    @Query("SELECT u FROM User u JOIN u.savedPostsIds p WHERE p = :postId")
    List<User> findUsersBySavedPostsContaining(@Param("postId") Long postId);


    Optional<User> findUserByPhoneNumber(String phoneNumber);
}