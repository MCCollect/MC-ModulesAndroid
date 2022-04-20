package com.mc.mcmodules.model.classes.library;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharePreference {

    private static final String _PREFERENCE = "SHARE_PREFERENCE";
    public static final String _PREFERENCE_FOTOS = "SHARE_PREFERENCE_FOTOS";
    private static SharePreference preference;
    private final SharedPreferences sharedPreferences;

    private SharePreference(Context context) {
        sharedPreferences = context.getSharedPreferences(_PREFERENCE, Context.MODE_PRIVATE);
    }

    public static SharePreference getInstance(Activity act) {
        if (preference == null) preference = new SharePreference(act);
        return preference;
    }

    public static SharePreference getInstance(Context context) {
        if (preference == null) preference = new SharePreference(context);
        return preference;
    }

    public void saveData(String key, String value) {
        SharedPreferences.Editor editSP = sharedPreferences.edit();
        editSP.putString(key, value);
        editSP.apply();
    }

    public void saveData(String key, Boolean value) {
        SharedPreferences.Editor editSP = sharedPreferences.edit();
        editSP.putBoolean(key, value);
        editSP.apply();
    }

    public void saveData(String key, int value) {
        SharedPreferences.Editor editSP = sharedPreferences.edit();
        editSP.putInt(key, value);
        editSP.apply();
    }

    public void saveData(String key, float value) {
        SharedPreferences.Editor editSP = sharedPreferences.edit();
        editSP.putFloat(key, value);
        editSP.apply();
    }

    public String getStrData(String key) {
        if (sharedPreferences != null) return sharedPreferences.getString(key, "");
        return "";
    }

    public boolean getBooData(String key) {
        return sharedPreferences != null && sharedPreferences.getBoolean(key, false);
    }

    public boolean getBooDataSendFake(String key) {
        return sharedPreferences != null && sharedPreferences.getBoolean(key, true);
    }

    public int getIntData(String key) {
        if (sharedPreferences != null) return sharedPreferences.getInt(key, 0);
        return 0;
    }

    public double getFloatData(String key) {
        if (sharedPreferences != null) return sharedPreferences.getFloat(key, 0);
        return 0;
    }

    public float getFloatDataNull(String key, float coordenada) {
        if (sharedPreferences != null) return sharedPreferences.getFloat(key, coordenada);
        return (float) 0;
    }

    public void clearSharedPreferencesForPictures() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public SharedPreferences getPref() {
        return sharedPreferences;
    }
}
