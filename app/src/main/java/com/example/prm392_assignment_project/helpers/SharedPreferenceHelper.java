package com.example.prm392_assignment_project.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {
    public static final String PREFERENCE_NAME = "USER_PREFERENCE";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor preferenceEditor;

    public SharedPreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        preferenceEditor = sharedPreferences.edit();
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public void putString(String key, String value) {
        preferenceEditor.putString(key, value);
        preferenceEditor.apply();
    }

    public String getString(String key) {
        if (!sharedPreferences.contains(key)) {
            return null;
        }

        return sharedPreferences.getString(key, null);
    }

    public void removePreference(String key) {
        if (!sharedPreferences.contains(key)) {
            return;
        }

        preferenceEditor.remove(key);
        preferenceEditor.apply();
    }
}
