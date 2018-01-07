package com.example.acer.monperabook.SQLite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.acer.monperabook.MainGatewayActivity;

/**
 * Created by Azhary Arliansyah on 25/11/2017.
 */

public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "AppPreferences";
    private static final String LOGGED_IN = "LoggedIn";
    private static final String USER_ID = "UserId";
    private static final String USERNAME = "Username";
    private static final String EMAIL = "Email";
    private static final String NAME = "Name";

    public SessionManager(Context context) {
        this.context = context;
        this.pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        this.editor = this.pref.edit();
    }

    public void createLoginSession(String userId, String username, String email, String name) {
        this.editor.putBoolean(LOGGED_IN, true);
        this.editor.putString(USER_ID, userId);
        this.editor.putString(USERNAME, username);
        this.editor.putString(EMAIL, email);
        this.editor.putString(NAME, name);
        this.editor.commit();
    }

    public String getUserId() {
        return this.pref.getString(USER_ID, "");
    }

    public boolean isUserLoggedIn() {
        return this.pref.getBoolean(LOGGED_IN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();

        Intent intent = new Intent(context, MainGatewayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
