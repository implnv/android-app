package com.example.sputnicjitsi.auth;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.Contract;

import java.util.Base64;

public class UserSpecialist {
    private static int userId;
    private static String firstName;
    private static String lastName;
    private static String patronymicName;
    private static String email;
    private static String organization_name;

    public static void setUserInfo(String token){
        userId = Integer.parseInt(getField(token, "uid").toString());
        firstName = getField(token, "first_name").toString();
        lastName = getField(token, "last_name").toString();
        patronymicName = getField(token, "patronymic").toString();
        email = getField(token, "email").toString();
        organization_name = getField(token, "organization_name").toString();
    }

    public static void setUserInfo(int userId,String firstName, String lastName, String patronymicName, String email){
        UserSpecialist.userId = userId;
        UserSpecialist.firstName = firstName;
        UserSpecialist.lastName = lastName;
        UserSpecialist.patronymicName = patronymicName;
        UserSpecialist.email = email;
    }

    @NonNull
    @Contract("_ -> new")
    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    private static JsonElement getField(@NonNull String token, @NonNull String field) {
        return new JsonParser().parse(decode(token.split("\\.")[1])).getAsJsonObject().get(field);
    }

    @NonNull
    @Contract(pure = true)
    public static String getUsername(){
        return lastName.replace('\"', ' ') + " " + firstName.replace('\"', ' ') + " | " + organization_name.replace('\"', ' ');
    }

    public static String getUserEmail(){
        return email;
    }

    public static int getUserId(){
        return userId;
    }

    public static String getPatronymicName() {
        return patronymicName;
    }
}
