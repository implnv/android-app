package com.example.sputnicjitsi.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sputnicjitsi.R;
import com.example.sputnicjitsi.WebSocket;
import com.example.sputnicjitsi.experts.AvailableExperts;
import com.example.sputnicjitsi.experts.ExpResponse;
import com.example.sputnicjitsi.experts.ExpertApi;
import com.example.sputnicjitsi.refresh.RefreshApi;
import com.example.sputnicjitsi.refresh.RefreshRequest;
import com.example.sputnicjitsi.refresh.RefreshResponse;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExpertsActivity extends AppCompatActivity {
    private String accessToken, refreshToken;
    private ExpertApi expertApi;
    private RefreshApi refreshApi;
    private int organizationId;
    private int uid;
    private RecyclerView recyclerView;
    private int sizePage, curPage, shiftList;
    private TextView page;
    private ImageView next, prev;
    private List<AvailableExperts> expertsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View windowDecorView = getWindow().getDecorView();
        windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        setContentView(R.layout.activity_list_experts);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.109:6868/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        expertApi = retrofit.create(ExpertApi.class);
        refreshApi = retrofit.create(RefreshApi.class);

        URL serverURL;
        try {
            serverURL = new URL("https://pseudogu.ru/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            accessToken = arguments.getString("accessToken");
            refreshToken = arguments.getString("refreshToken");
            uid = arguments.getInt("uid");
            organizationId = arguments.getInt("organization_id");
        }

        WebSocket.getInstance(accessToken);

        prev = findViewById(R.id.prev);
        prev.setVisibility(View.INVISIBLE);
        next = findViewById(R.id.next);
        next.setVisibility(View.INVISIBLE);
        page = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recyclerView2);

        curPage = 1;
        shiftList = 3;
        getExpertsList();
    }

    private void setPage() {
        if (expertsList.size() % 3 == 0) {
            sizePage = expertsList.size() / 3;
        } else {
            sizePage = expertsList.size() / 3 + 1;
        }

        String str = curPage + " / " + sizePage;
        page.setText(str);
    }

    private void setCurList () {
        if(curPage > 0 && curPage <= sizePage) {
            checkVisibleButton();
            List<AvailableExperts> cur = new ArrayList<>();

            for (int i = (curPage - 1) * shiftList; i < (curPage - 1) * shiftList + shiftList && i < expertsList.size(); i++) {
                cur.add(expertsList.get(i));
            }

            ExpertAdapter adapter = new ExpertAdapter(getApplicationContext(), cur);
            recyclerView.setAdapter(adapter);
        } else if (curPage > sizePage) {
            curPage = sizePage;
        } else {
            curPage = 1;
        }
    }

    private void checkVisibleButton() {
        if (curPage > 1 && curPage < sizePage) {
            prev.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
        } else if (curPage == 1) {
            prev.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
        } else {
            prev.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
        }
    }

    private void getExpertsList() {
        Call<ExpResponse> messages = expertApi.getExperts("Bearer "+ accessToken, organizationId, uid);
        messages.enqueue(new Callback<ExpResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExpResponse> call, @NonNull Response<ExpResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.code() == 200 && response.body().getStatus().equals("success")) {
                    expertsList = response.body().getPayload().getAvailableExperts();
                    setPage();
                    setCurList();
                } else if (response.code() == 401) {
                    refresh();
                    getExpertsList();
                } else {
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(response.body()).getErrors().toString(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ExpResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Ошибка сервера: "+ t, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void refresh() {
        RefreshRequest refresh = new RefreshRequest(refreshToken);
        Call<RefreshResponse> messages = refreshApi.refreshTokens(refresh);
        messages.enqueue(new Callback<RefreshResponse>() {
            @Override
            public void onResponse(@NonNull Call<RefreshResponse> call, @NonNull Response<RefreshResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.code() == 200 && response.body().getStatus().equals("success")) {
                    accessToken = response.body().getPayload().getAccessToken();
                    refreshToken = response.body().getPayload().getRefreshToken();
                } else  {
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(response.body()).getErrors().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RefreshResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Ошибка сервера: "+ t, Toast.LENGTH_LONG).show();
            }
        });
    }
    public void startJitsi(String str){
        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                .setRoom(str)
                .build();
        JitsiMeetActivity.launch(this, options);
    }

    public void clickNextButton(View view) {
        curPage++;
        getExpertsList();
        String str = curPage + "/" + sizePage;
        page.setText(str);
    }
    public void clickPrevButton(View view) {
        curPage--;
        getExpertsList();
        String str = curPage + "/" + sizePage;
        page.setText(str);
    }
    //TODO разобраться, как сделать кнопку
    public void callExp(@NonNull View view) {
        startJitsi("123");
    }
    //TODO разобраться со сворачиванием окна
    //TODO Jitsi сворачивание
}