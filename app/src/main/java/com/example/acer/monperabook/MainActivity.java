package com.example.acer.monperabook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.monperabook.CustomAdapter.Artifact;
import com.example.acer.monperabook.CustomAdapter.ArtifactsAdapter;
import com.example.acer.monperabook.Singleton.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ListView artifactsListView;
    private ArrayList<Artifact> artifacts = new ArrayList<>();
    private ArtifactsAdapter artifactsAdapter;
    private BottomNavigationView mBottomNav;
    private Context mContext;
    private ProgressDialog dialog;
    private String mEndpoint;
    private String TAG = "monperabook.artifact_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = (EditText) findViewById(R.id.search);

        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        mContext = this;
        mEndpoint = getString(R.string.server_ip);
        artifactsListView = (ListView) findViewById(R.id.list);

        // attach artifact item click listener
        artifactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent artifactDetailsIntent = new Intent(mContext, ArtifactDetailsActivity.class);
                Artifact artifact = (Artifact) artifactsListView.getItemAtPosition(position);

                artifactDetailsIntent.putExtra("nama", artifact.getTitle());
                artifactDetailsIntent.putExtra("deskripsi", artifact.getDescription());

                startActivity(artifactDetailsIntent);
            }
        });

        ShowDialog("Loading...", true);

        String requestUrl = mEndpoint + "artifak/get-artifak";
        JsonObjectRequest getArtifactList = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ShowDialog("", false);
                        try {
                            JSONArray artifactList = response.getJSONArray("data");
                            for (int i = 0; i < artifactList.length(); i++) {
                                JSONObject artifact = artifactList.getJSONObject(i);
                                artifacts.add(new Artifact(artifact.getString("nama"), artifact.getString("deskripsi")));
                            }

                            artifactsAdapter = new ArtifactsAdapter(mContext, artifacts);
                            artifactsListView.setAdapter(artifactsAdapter);

                            searchEditText.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                                @Override
                                public void afterTextChanged(Editable s) {
                                    String query = searchEditText.getText().toString();
                                    artifactsAdapter.filter(query);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            String msg = e.getMessage();
                            if (msg != null) {
                                Log.e(TAG, msg);
                            } else {
                                Log.e(TAG, "JSONException Unknown Error!");
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ShowDialog("", false);
                        String msg = error.getMessage();
                        if (msg != null) {
                            Log.e(TAG, msg);
                        } else {
                            Log.e(TAG, "Unknown Error!");
                        }
                    }
                });

        AppSingleton.getInstance(this).addToRequestQueue(getArtifactList, TAG);
    }

    private void ShowDialog(String msg, boolean check) {
        if (check) {
            dialog.setMessage(msg);
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }
}
