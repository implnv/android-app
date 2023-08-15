package com.example.sputnicjitsi.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sputnicjitsi.QR.QRCodeContextAnalyzer;
import com.example.sputnicjitsi.QR.QRCodeFoundListener;
import com.example.sputnicjitsi.QR.QRCodeImageAnalyzer;
import com.example.sputnicjitsi.R;
import com.example.sputnicjitsi.Retro;
import com.example.sputnicjitsi.auth.AuthRequest;
import com.example.sputnicjitsi.auth.AuthResponse;
import com.example.sputnicjitsi.auth.UserApi;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.Contract;

import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private volatile String qrCode;
    private UserApi userApi;
    private WifiManager wifiManager;
    private final QRCodeContextAnalyzer analyzer = new QRCodeContextAnalyzer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.activity_main_previewView);
        userApi = Retro.getInstance().create(UserApi.class);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        startWifi();
        Toast.makeText(getApplicationContext(), "Отсканируйте QR-код для входа в систему", Toast.LENGTH_LONG).show();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        requestCamera();
    }
    //включение Wi-Fi
    private void startWifi() {
        if (checkWifiOnAndConnected() && wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
            Toast.makeText(getApplicationContext(), "Нет подключения к Wi-Fi", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                wifiManager.setWifiEnabled(true);
                Toast.makeText(getApplicationContext(),"SDK < 29", Toast.LENGTH_SHORT).show();
            } else {
                Intent panel = new Intent(Settings.Panel.ACTION_WIFI);
                startActivity(panel);
            }
        }
    }
    // если QR-код - лог/пас
    private void auth() {
        AuthRequest adm = new AuthRequest(analyzer.getQrLogIn().getLogin(), analyzer.getQrLogIn().getPass());
        Call<AuthResponse> messages = userApi.authorization(adm);
        messages.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.code() == 200 && response.body().getStatus().equals("success")) {
                    if (isSpecialist(response.body().getPayload().getAccessToken())) {
                        logIn(response.body().getPayload().getAccessToken(), response.body().getPayload().getRefreshToken());
                    } else {
                        Toast.makeText(getApplicationContext(), "Невозможно выполнить вход под данной учетной записью", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(response.body()).getErrors().toString(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(),"Ошибка сервера: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }
    @NonNull
    @Contract("_ -> new")
    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }
    private boolean isSpecialist (@NonNull String token) {
        String[] parts = token.split("\\.");
        return decode(parts[1]).contains("Specialist");
    }

    private JsonElement getField(@NonNull String token, @NonNull String field) {
        return new JsonParser().parse(decode(token.split("\\.")[1])).getAsJsonObject().get(field);
    }
    private int getUid(@NonNull String token) {
        return Integer.parseInt(getField(token, "uid").toString());
    }

    @NonNull
    private int getOrganizationId(@NonNull String token) {
        return Integer.parseInt(getField(token, "organization_id").toString());
    }

    //вход в аккаунт и переход в новый активити
    private void logIn(@NonNull String access, String refresh) {
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = pref.edit();
//
//        if (access.length() != 0) {
//            String[] arrAccessDecoded = access.split("\\.");
//            String strAccessDecoded = decode(arrAccessDecoded[1]);
//
//            User jsonAccessDecoded = new Gson().fromJson(new Gson().toJson(strAccessDecoded).toString(), User.class);
//            Toast.makeText(this, jsonAccessDecoded.getFirstName(), Toast.LENGTH_LONG).show();
//        }

//        editor.putString("LOGIN", decode(access));
//        editor.putString("PASSWORD", );

        Intent intent = new Intent(this, ExpertsActivity.class);
        intent.putExtra("accessToken", access);
        intent.putExtra("refreshToken", refresh);
        intent.putExtra("uid", getUid(access));
        intent.putExtra("organization_id", getOrganizationId(access));
        startActivity(intent);
    }
    // подключен ли Wi-Fi, есть ли доступ в интернет
    private boolean checkWifiOnAndConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return true;
        }
        Network network = connMgr.getActiveNetwork();
        if (network == null) {
            return true;
        }
        NetworkCapabilities capabilities = connMgr.getNetworkCapabilities(network);
        return capabilities == null || !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }
    // подключение к wi-fi по QR-code
    private void connectNetwork() {
        // TODO добавить проверку, подключена ли уже сеть
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkWifiOnAndConnected()){
                startWifi();
            }
            final NetworkSpecifier specifier;
            if (Objects.equals(analyzer.getQrWifi().getTechnology(), "WPA2") || Objects.equals(analyzer.getQrWifi().getTechnology(), "WPA")) {
                specifier = new WifiNetworkSpecifier.Builder()
                            .setSsid(analyzer.getQrWifi().getSsid())
                            .setWpa2Passphrase(analyzer.getQrWifi().getPass())
                            .build();
            } else {
                specifier = new WifiNetworkSpecifier.Builder()
                                .setSsid(analyzer.getQrWifi().getSsid())
                                .setWpa3Passphrase(analyzer.getQrWifi().getPass())
                                .build();
            }
            final NetworkRequest request = new NetworkRequest.Builder()
                                                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                                                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                                .setNetworkSpecifier(specifier)
                                                .build();

            final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback();
            connectivityManager.requestNetwork(request, networkCallback);
            if (checkWifiOnAndConnected()) {
                Toast.makeText(getApplicationContext(), "Не удалось подключиться", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //проверка на доступ к камере и запрос на разрешение
    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();  // если разрешение есть
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
    // запуск камеры
    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Ошибка запуска камеры: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }
    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {

        previewView.setImplementationMode(PreviewView.ImplementationMode.PERFORMANCE);

        Preview preview = new Preview.Builder()
                                        .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                                                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
                    //TODO: разобраться с async (+/-)
                    @Override
                    public void onQRCodeFound(String _qrCode) {
                        if (!Objects.equals(qrCode, _qrCode)) {
                            qrCode = _qrCode;
                            if (QRCodeContextAnalyzer.isWiFi(qrCode)) {
                                startWifi();
                                analyzer.addQr(qrCode);
                                connectNetwork();
                            } else if (QRCodeContextAnalyzer.isLogin(qrCode)) {
                                analyzer.addQr(qrCode);
                                auth();
                            } else Toast.makeText(getApplicationContext()
                                    , "Неверный QR-код", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void qrCodeNotFound() {
                        qrCode = null;
                    }
                }));
        ImageCapture imageCapture = new ImageCapture.Builder().setFlashMode(ImageCapture.FLASH_MODE_ON).build();
        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);
    }
    //TODO разобраться со сворачиванием окна
    //TODO фонарик
}