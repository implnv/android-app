package com.example.sputnicjitsi.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.sputnicjitsi.WebSocket;
import com.example.sputnicjitsi.auth.UserSpecialist;
import com.facebook.react.modules.core.PermissionListener;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;

import java.net.MalformedURLException;
import java.net.URL;


public class ConferenceActivity extends JitsiMeetActivity implements JitsiMeetActivityInterface {
    private JitsiMeetView view;

    @Override
    public void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JitsiMeetActivityDelegate.onActivityResult(
                this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        JitsiMeetActivityDelegate.onBackPressed();
    }

    private String args;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getIntent().getExtras();
        if(arguments != null)
        {
            args = arguments.getString("args");
        }
        URL serverURL;
        try {
            serverURL = new URL("https://pseudogu.ru/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        view = new JitsiMeetView(this);
        JitsiMeetUserInfo info = new JitsiMeetUserInfo();
        info.setDisplayName(UserSpecialist.getUsername());
        JitsiMeetConferenceOptions options  = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setFeatureFlag("welcomepage.enabled", false)
                .setFeatureFlag("toolbox.enabled", false)
                .setFeatureFlag("filmstrip.enabled", false)
                .setFeatureFlag("chat.enabled", false)
                .setFeatureFlag("lobby-mode.enabled", false)
                .setFeatureFlag("help.enabled", false)
                .setFeatureFlag("overflow-menu.enabled", false)
                .setFeatureFlag("prejoinpage.enabled", false)
                .setFeatureFlag("pip.enabled", false)
                .setFeatureFlag("pip-while-screen-sharing.enabled", false)
                .setFeatureFlag("call-integration.enabled", false)
                .setRoom(args)
                .setUserInfo(info)
                .build();
        listenSocket();
        view.join(options);
        setContentView(view);
    }

    private void listenSocket(){
        WebSocket.getInstance().on("end a call with a expert", args -> onDestroy());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        view.dispose();
        view = null;
        JitsiMeetActivityDelegate.onHostDestroy(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        JitsiMeetActivityDelegate.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            @NonNull final String[] permissions,
            @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();

        JitsiMeetActivityDelegate.onHostResume(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        JitsiMeetActivityDelegate.onHostPause(this);
    }

    @Override
    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {
        super.requestPermissions(strings, i);
    }
}