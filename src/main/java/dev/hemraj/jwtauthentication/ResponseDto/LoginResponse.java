package dev.hemraj.jwtauthentication.ResponseDto;

import java.time.ZonedDateTime;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private ZonedDateTime expiresIn;
    private String userName;
    private String userId;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ZonedDateTime getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(ZonedDateTime expiresIn) {
        this.expiresIn = expiresIn;
    }
}
