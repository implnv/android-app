package com.example.sputnicjitsi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retro {
    private static String URI = "http://192.168.1.109:6868/api/v1/";
    private static volatile Retrofit instance;
    private static void Retrofit() {}
    public static Retrofit getInstance() {
        Retrofit lInstance = instance;

        if (lInstance == null) {
            synchronized (WebSocket.class) {
                lInstance = instance;

                if (lInstance == null) {
                    instance = lInstance = new Retrofit.Builder().baseUrl(URI).addConverterFactory(GsonConverterFactory.create()).build();
                }
            }
        }

        return lInstance;
    }
}
