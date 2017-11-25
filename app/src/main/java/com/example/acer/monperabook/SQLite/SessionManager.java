package com.example.acer.monperabook.SQLite;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Azhary Arliansyah on 25/11/2017.
 */

public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "AppPreferences";

    public SessionManager(Context context) {
        this.context = context;
        this.pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        this.editor = this.pref.edit();
    }

    public void createLoginSession(String userId, String username, String email, String name) {
        this.editor.putBoolean("LoggedIn", true);
        this.editor.putString("UserId", userId);
        this.editor.putString("Username", username);
        this.editor.putString("Email", email);
        this.editor.putString("Name", name);
        this.editor.commit();
    }

    public boolean isUserLoggedIn() {
        return this.pref.getBoolean("LoggedIn", false);
    }
}
