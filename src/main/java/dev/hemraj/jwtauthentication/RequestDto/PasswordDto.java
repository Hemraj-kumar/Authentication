package dev.hemraj.jwtauthentication.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PasswordDto {
    private String confirm_password;
    private String new_password;

}
