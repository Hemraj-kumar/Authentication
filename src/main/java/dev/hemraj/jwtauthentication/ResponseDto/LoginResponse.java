package dev.hemraj.jwtauthentication.ResponseDto;

import java.time.ZonedDateTime;
import java.util.Date;

public class LoginResponse {
    private String token;
    private ZonedDateTime expiresIn;
    private String userName;
    private String userId;

    public String getUserid() {
        return userId;
    }

    public void setUserid(String userid) {
        this.userId = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ZonedDateTime getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(ZonedDateTime expiresIn) {
        this.expiresIn = expiresIn;
    }
}
