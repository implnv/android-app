package com.example.sputnicjitsi.auth;

import com.example.sputnicjitsi.auth.AuthRequest;
import com.example.sputnicjitsi.auth.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {
    @POST("login")
    Call<AuthResponse> authorization(@Body AuthRequest auth);
}
