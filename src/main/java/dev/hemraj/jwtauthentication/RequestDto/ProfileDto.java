package dev.hemraj.jwtauthentication.RequestDto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileDto {
    private String designation;
    private String location;
    private String about;
}
