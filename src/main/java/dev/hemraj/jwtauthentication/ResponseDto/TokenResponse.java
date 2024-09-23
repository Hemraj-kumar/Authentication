package dev.hemraj.jwtauthentication.ResponseDto;

import java.time.ZonedDateTime;

public class TokenResponse {
    private Enum status;
    private String accessToken;
    private String refreshToken;
    private ZonedDateTime expiresAt;
    private String description;
    private boolean success;
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Enum getStatus() {
        return status;
    }

    public void setStatus(Enum status) {
        this.status = status;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public TokenResponse(Enum status, String accessToken, String refreshToken, ZonedDateTime expiresAt, String description, boolean success) {
        this.status = status;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.description = description;
        this.success = success;
    }

    public TokenResponse() {
    }
}
