package com.valstrike16;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class Gyroscope implements SensorEventListener {
    private Context context;
    private SensorManager sensorManager;
    private Sensor gyroSensor;

    public Gyroscope(Context context) {
        this.context = context;
        // const-string v0, "sensor"
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        // const/4 v1, 0x4 -> Sensor.TYPE_GYROSCOPE is 4
        this.gyroSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    // Start listening to gyroscope data
    public void start() {
        // const/4 v2, 0x1 -> SENSOR_DELAY_FASTEST
        this.sensorManager.registerListener(this, this.gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    // Stop listening to gyroscope data
    public void stop() {
        this.sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];

        // Get screen rotation
        WindowManager windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int rotation = display.getRotation();

        // Handle Axis inversion based on screen rotation from Smali
        if (rotation == Surface.ROTATION_0) { // 0
            x = -x;
        } else if (rotation == Surface.ROTATION_90) { // 1
            // Values stay as they are
        } else if (rotation == Surface.ROTATION_180) { // 2
            y = -y;
        } else if (rotation == Surface.ROTATION_270) { // 3
            x = -x;
            y = -y;
        }

        // Send modified values to native engine
        nativeOnGyroscopeChanged(x, y);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    // Send gyroscope data to the engine via JNI (C++)
    private native void nativeOnGyroscopeChanged(float x, float y);
}
