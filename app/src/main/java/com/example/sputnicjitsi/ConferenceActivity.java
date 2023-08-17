package com.example.sputnicjitsi;

import android.os.Bundle;
import android.util.Log;

import org.jitsi.meet.sdk.JitsiMeetActivity;

public class ConferenceActivity extends JitsiMeetActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       listenSocket();
    }

    private void listenSocket(){
        WebSocket.getInstance().on("end a call with a specialist", args -> {
            ConferenceActivity.super.onDestroy();
            Log.println(Log.ASSERT, "Destroy", "success");
        });
    }
}