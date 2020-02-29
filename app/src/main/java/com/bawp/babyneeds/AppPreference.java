package com.bawp.babyneeds;

import android.content.Context;
import android.content.SharedPreferences;

class AppPreference {
    private final SharedPreferences preferences;
    private final String initialRun = "initial_run";
    private final String intervalReset = "interval_reset";

    AppPreference(Context context) {
        preferences = context.getSharedPreferences("baby", Context.MODE_PRIVATE);
    }

    boolean isInitialRun() {
        return preferences.getBoolean(initialRun, true);
    }

    void setInitialRun(boolean value) {
        preferences.edit().putBoolean(initialRun, value).apply();
    }

    boolean isIntervalReset() {
        return preferences.getBoolean(intervalReset, false);
    }

    void setIntervalReset() {
        preferences.edit().putBoolean(intervalReset, true).apply();
    }

    void resetWorkerPreferences() {
        preferences.edit().putBoolean(initialRun, true).apply();
        preferences.edit().putBoolean(intervalReset, false).apply();
    }

    String getAlarmState() {
        String state = "alarm_state";
        return preferences.getString(state, "Alarm on");
    }
}
