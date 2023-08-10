package com.example.sputnicjitsi.experts;

import androidx.annotation.NonNull;

public class Error {
    private String massage;

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    @NonNull
    @Override
    public String toString() {
        return massage;
    }
}
