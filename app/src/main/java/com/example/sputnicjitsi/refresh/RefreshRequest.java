package com.example.sputnicjitsi.refresh;

public class RefreshRequest {
    private String refreshToken;

    public RefreshRequest(String refreshToken){
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
