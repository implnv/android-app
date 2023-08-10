package com.example.sputnicjitsi.refresh;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RefreshApi {
    @POST("refreshTokens")
    Call<RefreshResponse> refreshTokens(@Header("Authorization") RefreshRequest refresh);
}
