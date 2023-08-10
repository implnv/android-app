package com.example.sputnicjitsi;

import android.text.TextUtils;

import java.net.URI;
import java.util.Collections;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class WebSocket {
    private static volatile URI uri = URI.create("http://192.168.1.109:7978");
    private static volatile Socket instance;
    private static volatile String token;

    private WebSocket() {}

//    public static Socket getInstance(String t) {
//        if (!TextUtils.isEmpty(t)) {
//            IO.Options opts = IO.Options.builder().setAuth(Collections.singletonMap("token", t)).build();
//            instance = IO.socket(uri, opts).disconnect().connect();
//        }
//
//        return instance;
//    }

    public static Socket getInstance(String t) {
        Socket lInstance = instance;
        IO.Options opts = IO.Options.builder().setAuth(Collections.singletonMap("token", t)).build();

        if (lInstance == null) {
            synchronized (WebSocket.class) {

                if (lInstance == null) {
                    if (!TextUtils.isEmpty(t)) {
                        instance = lInstance = IO.socket(uri, opts).disconnect().connect();
                    } else {
                        throw new RuntimeException("Токен не установлен!");
                    }
                }
            }
        }

        return lInstance;
    }

    public static Socket getInstance() {
        Socket lInstance = instance;

        if (lInstance == null) {
            synchronized (WebSocket.class) {
                lInstance = instance;

                if (lInstance == null) {
                    if (!TextUtils.isEmpty(token)) {
                        IO.Options opts = IO.Options.builder().setAuth(Collections.singletonMap("token", token)).build();
                        instance = lInstance = IO.socket(uri, opts).connect();
                    } else {
                        throw new RuntimeException("Токен не установлен!");
                    }
                }
            }
        }

        return lInstance;
    }
}
