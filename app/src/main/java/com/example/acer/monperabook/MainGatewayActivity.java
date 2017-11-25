package com.example.acer.monperabook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.acer.monperabook.SQLite.SessionManager;

/**
 * Created by Azhary Arliansyah on 25/11/2017.
 */

public class MainGatewayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent activityIntent;
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isUserLoggedIn()) {
            activityIntent = new Intent(this, MainActivity.class);
        } else {
            activityIntent = new Intent(this, LoginActivity.class);
        }
        startActivity(activityIntent);
        finish();
    }
}
