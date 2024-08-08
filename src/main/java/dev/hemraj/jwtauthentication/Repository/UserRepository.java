package dev.hemraj.jwtauthentication.Repository;

import dev.hemraj.jwtauthentication.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    boolean existsByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    User findUserByEmailIgnoreCase(String email);
    User findUserById(Integer userId);
}

