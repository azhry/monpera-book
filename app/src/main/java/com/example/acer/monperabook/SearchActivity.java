package com.example.acer.monperabook;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.monperabook.CustomAdapter.Artifact;
import com.example.acer.monperabook.CustomAdapter.ArtifactsRecyclerAdapter;
import com.example.acer.monperabook.Singleton.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Azhary Arliansyah on 27/05/2018.
 */

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "SearchActivity";
    private String mEndpoint;
    private ArtifactsRecyclerAdapter artifactRecyclerAdapter;
    private RecyclerView.LayoutManager artifactLayoutManager;
    private RecyclerView artifactRecyclerView;
    private ArrayList<Artifact> artifacts = new ArrayList<>();
    private Context activityContext;
    private EditText searchEditText;
    private ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        activityContext = this;
        mEndpoint = getString(R.string.server_ip);

        initUI();
        fetchData();
    }

    private void initUI() {

        artifactLayoutManager = new LinearLayoutManager(getApplicationContext());
        artifactRecyclerView = (RecyclerView)findViewById(R.id.artifact_recycler_view);
        artifactRecyclerView.setLayoutManager(artifactLayoutManager);

        searchEditText = (EditText)findViewById(R.id.searchEditText);
        searchButton = (ImageButton)findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String q = searchEditText.getText().toString();
                filter(q);

            }
        });

    }

    private void filter(String query) {
        ArrayList<Artifact> temp = new ArrayList();
        for(Artifact d: artifacts){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getTitle().toLowerCase().contains(query.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        artifactRecyclerAdapter.updateList(temp);
    }

    private void fetchData() {

        String requestURL = mEndpoint + "artifak/get-artifak";
        JsonObjectRequest getFavorites = new JsonObjectRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean error = response.getBoolean("error");
                            if (!error) {

                                artifacts.clear();
                                JSONArray artifactList = response.getJSONArray("data");
                                for (int i = 0; i < artifactList.length(); i++) {

                                    JSONObject artifact = artifactList.getJSONObject(i);
                                    artifacts.add(new Artifact(artifact.getString("kode_artifak"),
                                            artifact.getString("nama"), artifact.getString("deskripsi"),
                                            artifact.getString("like"), artifact.getString("foto"),
                                            artifact.getString("kategori")));

                                }

                                artifactRecyclerAdapter = new ArtifactsRecyclerAdapter(artifacts, activityContext);
                                artifactRecyclerView.setAdapter(artifactRecyclerAdapter);
                                artifactRecyclerAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(activityContext, "Request error", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = error.getMessage();
                        if (msg != null) {
                            Log.e(TAG, msg);
                        }
                    }
                });
        AppSingleton.getInstance(activityContext).addToRequestQueue(getFavorites, TAG);

    }

}
