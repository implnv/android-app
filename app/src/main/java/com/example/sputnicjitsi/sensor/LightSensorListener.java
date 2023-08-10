package com.example.sputnicjitsi.sensor;

import static org.webrtc.ContextUtils.getApplicationContext;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;

public class LightSensorListener implements SensorEventListener {
    public boolean isDark;

    @Override
    public void onSensorChanged(@NonNull SensorEvent event) {
        if(event.values.length > 0) {
            isDark = event.values[0] <= SensorManager.LIGHT_SUNRISE;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
