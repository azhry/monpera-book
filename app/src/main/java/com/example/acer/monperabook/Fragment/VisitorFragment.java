package com.example.acer.monperabook.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.monperabook.CustomAdapter.Artifact;
import com.example.acer.monperabook.CustomAdapter.ArtifactsRecyclerAdapter;
import com.example.acer.monperabook.R;
import com.example.acer.monperabook.SQLite.SessionManager;
import com.example.acer.monperabook.Singleton.AppSingleton;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Azhary Arliansyah on 25/03/2018.
 */

public class VisitorFragment extends Fragment {

    public static final String TAG = "VisitorFragment";
    private String mEndpoint;
    private ArtifactsRecyclerAdapter artifactRecyclerAdapter;
    private RecyclerView.LayoutManager artifactLayoutManager;
    private RecyclerView artifactRecyclerView;
    private ArrayList<Artifact> artifacts;

    public VisitorFragment() {
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
        return inflater.inflate(R.layout.visitor_fragment, container, false);
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
        artifactRecyclerView = (RecyclerView)getActivity().findViewById(R.id.artifact_recycler_view2);
        artifactRecyclerView.setLayoutManager(artifactLayoutManager);
        getVisitorFavorite(view);
    }

    private void getVisitorFavorite(final View view) {

        String requestURL = mEndpoint + "artifak/most-favorite?limit=10";
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
                });
        AppSingleton.getInstance(view.getContext()).addToRequestQueue(getFavorites, TAG);

    }

}
