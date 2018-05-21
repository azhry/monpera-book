package com.example.acer.monperabook.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.acer.monperabook.CustomAdapter.Artifact;
import com.example.acer.monperabook.CustomAdapter.ArtifactsRecyclerAdapter;
import com.example.acer.monperabook.R;
import com.example.acer.monperabook.SQLite.SessionManager;
import com.example.acer.monperabook.Singleton.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Azhary Arliansyah on 25/03/2018.
 */

public class MyFavoriteFragment extends Fragment {

    public static final String TAG = "MyFavoriteFragment";
    private String mEndpoint;
    private ArtifactsRecyclerAdapter artifactRecyclerAdapter;
    private RecyclerView.LayoutManager artifactLayoutManager;
    private RecyclerView artifactRecyclerView;
    private ArrayList<Artifact> artifacts;

    public MyFavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEndpoint = getString(R.string.server_ip);
        artifacts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.favorite_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            renderUI(view);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    private void renderUI(View view) {
        artifactLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        artifactRecyclerView = (RecyclerView)getActivity().findViewById(R.id.artifact_recycler_view);
        artifactRecyclerView.setLayoutManager(artifactLayoutManager);
        getMyFavorite(view);
    }

    private void getMyFavorite(final View view) {

        String requestURL = mEndpoint + "artifak/my-favorite";
        StringRequest getFavorites = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            boolean error = res.getBoolean("error");
                            if (!error) {
                                artifacts.clear();
                                JSONArray artifactList = res.getJSONArray("data");
                                for (int i = 0; i < artifactList.length(); i++) {

                                    JSONObject artifact = artifactList.getJSONObject(i);
                                    artifacts.add(new Artifact(artifact.getString("kode_artifak"),
                                            artifact.getString("nama"), artifact.getString("deskripsi"),
                                            artifact.getString("like"), artifact.getString("foto"),
                                            artifact.getString("kategori")));

                                }

                                artifactRecyclerAdapter = new ArtifactsRecyclerAdapter(artifacts, view.getContext());
                                artifactRecyclerView.setAdapter(artifactRecyclerAdapter);
                                artifactRecyclerAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(view.getContext(), "Request error", Toast.LENGTH_SHORT).show();
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
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                SessionManager sessionManager = new SessionManager(getContext());
                params.put("id_user", sessionManager.getUserId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SessionManager sessionManager = new SessionManager(getContext());
                params.put("id_user", sessionManager.getUserId());
                return params;
            }
        };
        AppSingleton.getInstance(view.getContext()).addToRequestQueue(getFavorites, TAG);

    }

}
