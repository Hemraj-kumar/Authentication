package dev.hemraj.jwtauthentication.Repository;

import dev.hemraj.jwtauthentication.Model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,String> {
    ConfirmationToken findByConfirmationToken(String confirmationToken);
//    Optional<ConfirmationToken> findByConfirmationTokenAndTokenType(String cToken);
    boolean existsByConfirmationToken(String token);
}
