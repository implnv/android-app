package com.example.sputnicjitsi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import io.socket.emitter.Emitter;

public class CallActivity extends AppCompatActivity {

    private int num;
    private String fio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_activity);

        View windowDecorView = getWindow().getDecorView();
        windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        setContentView(R.layout.call_activity);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Bundle arguments = getIntent().getExtras();
        if(arguments != null)
        {
            num = Integer.parseInt(Objects.requireNonNull(arguments.getString("num")));
            fio = arguments.getString("fio");
        }
        TextView text = findViewById(R.id.text_name_exp);
        text.setText(getName());
        sendSocket();
        myTimer();

        WebSocket.getInstance().on("end a call with a specialist", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onDestroy();
            }
        });
    }

    private void sendSocket(){

        try {
            WebSocket.getInstance().emit("call expert", new JSONObject().put("callId", num));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            URL serverURL = new URL("https://pseudogu.ru/");

            WebSocket.getInstance().on("set room", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JitsiMeetUserInfo info = new JitsiMeetUserInfo();
                    info.setDisplayName(fio);
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
                            .setRoom(args[0].toString())
                            .setUserInfo(info)
                            .build();
                    finish();
                    JitsiMeetActivity.launch(getApplicationContext(), options);
                }
            });

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    public void onButtonClick(View view) {
        finish();

        try {
            WebSocket.getInstance().emit("reject from specialist", new JSONObject().put("callId", num));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @NonNull
    private String getName(){
        String[] arrName = fio.split(" ");
        if(arrName[0].charAt(arrName.length - 1) == 'о') {
            return "Звонок " + arrName[0] + arrName[1].charAt(0)+ ". " +  arrName[2].charAt(0)+ ". ";
        } else
            return "Звонок " + arrName[0] + "у " + arrName[1].charAt(0)+ ". " +  arrName[2].charAt(0)+ ". ";
    }

    private void myTimer(){
        TextView text = findViewById(R.id.text_timer);
        ProgressBar progressBar = findViewById(R.id.determinateBar);
        CountDownTimer timer = new CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                text.setText("0:" + millisUntilFinished/1000);
                progressBar.setProgress((int) (millisUntilFinished/1000));
            }
            @Override
            public void onFinish() {
                synchronized(CallActivity.class) {
                    finish();
                    try {
                        WebSocket.getInstance().emit("reject from specialist", new JSONObject().put("callId", num));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        timer.start();
    }
}
