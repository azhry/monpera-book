package com.example.acer.monperabook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.acer.monperabook.SQLite.SessionManager;
import com.example.acer.monperabook.Singleton.AppSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Azhary Arliansyah on 25/11/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private Context mContext;
    private ProgressDialog progressDialog;
    private String endpoint;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        endpoint = getString(R.string.server_ip);
        usernameEditText = (EditText)findViewById(R.id.username);
        passwordEditText = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.loginBtn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        usernameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    login();
                    return true;
                }

                return false;
            }
        });

        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    login();
                    return true;
                }

                return false;
            }
        });
    }

    private void login() {
        progressDialog.setMessage("Logging in..");
        progressDialog.show();

        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        String URL = endpoint + "/login";

        StringRequest loginRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            if (error) {
                                Toast.makeText(mContext, jsonObject.getString("error_message"), Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject data = new JSONObject(jsonObject.getString("data"));
                                int userId = data.getInt("id_user");
                                String username = data.getString("username");
                                String email = data.getString("email");
                                String name = data.getString("name");
                                SessionManager sessionManager = new SessionManager(mContext);
                                sessionManager.createLoginSession(String.valueOf(userId), username, email, name);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSON_EXCEPTION", e.getMessage());
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = error.getMessage();
                        if (msg != null) {
                            Log.e("ERR_LOGIN", msg);
                        }
                        Toast.makeText(mContext, "Unknown error", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        AppSingleton.getInstance(mContext).addToRequestQueue(loginRequest, "LOGIN_REQUEST");
    }
}
