package com.example.ecosmart;

import android.content.Context;
import android.content.SharedPreferences;
public class SessionManager {

    private static final String PREF_NAME = "EcoSmartSession";
    private static final String KEY_USERNAME = "username";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getUsername() != null;
    }
}