package dev.hemraj.jwtauthentication.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;


@Table(name = "changePassword")
@Entity
@Data
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String email;
    private String newPassword;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String confirm_newPassword;
    @Column(unique = true,nullable = false)
    private String token;
    public ForgotPassword(String email, LocalDateTime createdAt, LocalDateTime expiresAt,String token){
        this.email = email;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.token = token;
    }
    public ForgotPassword(){}
}
