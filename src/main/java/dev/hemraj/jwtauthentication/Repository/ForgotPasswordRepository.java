package dev.hemraj.jwtauthentication.Repository;

import dev.hemraj.jwtauthentication.Model.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {
    ForgotPassword findForgotPasswordByToken(String token);
}
