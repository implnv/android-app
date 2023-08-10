package com.example.sputnicjitsi.experts;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ExpertApi {
    @GET("organizations/{organization_id}/s/{userId}/getAvailableExperts/")
    Call<ExpResponse> getExperts(@Header("Authorization") String authorization, @Path("organization_id") int organization_id, @Path("userId") int userId);
}
