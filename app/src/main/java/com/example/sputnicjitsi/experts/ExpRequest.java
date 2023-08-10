package com.example.sputnicjitsi.experts;

public class ExpRequest {
    private String access_token;
    public ExpRequest(String accessToken) {
        this.access_token = accessToken;
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }
}
