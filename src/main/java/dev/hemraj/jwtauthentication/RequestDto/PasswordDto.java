package dev.hemraj.jwtauthentication.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PasswordDto {
    private String new_password;
    private String confirm_password;
}
