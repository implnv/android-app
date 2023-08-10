package com.example.sputnicjitsi.auth;

import com.example.sputnicjitsi.QR.QRCodeContextAnalyzer;

public class AuthRequest {
    private String email;
    private String password;

    public AuthRequest (String email, String password){
       this.email = email;
       this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
