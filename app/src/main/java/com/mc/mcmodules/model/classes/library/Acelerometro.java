package com.mc.mcmodules.model.classes.library;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

public class Acelerometro implements SensorEventListener {

    /* DECLARACION DE OBJETOS */

    private final Context context;
    private final SensorManager mSensorManager;

    /* DECLARACION DE VARIABLES */

    private double ejex;
    private double ejey;
    private double ejez;

    public Acelerometro(Context context) {

        this.context = context;

        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if (sensors.size() > 0)
            mSensorManager.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);

        final SensorManager mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mSensorManager.unregisterListener(this,null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    ejex = event.values[SensorManager.DATA_X]; // Acceleration minus Gx on the x-axis
                    ejey = event.values[SensorManager.DATA_Y]; // Acceleration minus Gy on the y-axis
                    ejez = event.values[SensorManager.DATA_Z]; // Acceleration minus Gz on the z-axis
                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void desactivarSensores(){
        mSensorManager.unregisterListener((SensorEventListener) context,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    public double getEjex() {
        return ejex;
    }

    public double getEjey() {
        return ejey;
    }

    public double getEjez() {
        return ejez;
    }
}
