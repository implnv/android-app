package com.example.sputnicjitsi.refresh;

import com.example.sputnicjitsi.auth.Error;
import com.example.sputnicjitsi.auth.Payload;

import java.util.List;

public class RefreshResponse {
    private Payload payload;
    private String status;
    private List<Error> errors;

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
