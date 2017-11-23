package com.example.acer.monperabook.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.monperabook.ArtifactDetailsActivity;
import com.example.acer.monperabook.CustomAdapter.Artifact;
import com.example.acer.monperabook.CustomAdapter.ArtifactsAdapter;
import com.example.acer.monperabook.R;
import com.example.acer.monperabook.SQLite.DBHelper;
import com.example.acer.monperabook.Singleton.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Azhary Arliansyah on 12/11/2017.
 */

public class MenuFragment extends Fragment {

    private static final String ARG_TEXT = "arg_text";
    private static final String ARG_COLOR = "arg_color";

    private String mText;
    private int mColor;

    private View mContent;
    private TextView mTextView;

    private EditText searchEditText;
    private ListView artifactsListView;
    private ArrayList<Artifact> remoteArtifacts = new ArrayList<>();
    private ArrayList<Artifact> localArtifacts = new ArrayList<>();
    private ArtifactsAdapter artifactsAdapter;
    private ProgressDialog dialog;
    private String mEndpoint;
    private String TAG = "monperabook.artifact_list";
    private DBHelper db;

    private static String type;

    public static Fragment newInstance(String type) {
        Fragment frag = new MenuFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        frag.setArguments(args);
        MenuFragment.type = type;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new ProgressDialog(view.getContext());
        dialog.setCancelable(false);

        searchEditText = (EditText) view.findViewById(R.id.search);
        mEndpoint = getString(R.string.server_ip);
        artifactsListView = (ListView) view.findViewById(R.id.list);

        switch (type) {
            case "remote":

                // attach artifact item click listener
                artifactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        Intent artifactDetailsIntent = new Intent(view.getContext(), ArtifactDetailsActivity.class);
                        Artifact artifact = (Artifact) artifactsListView.getItemAtPosition(position);

                        artifactDetailsIntent.putExtra("kode_artifak", artifact.getCode());
                        artifactDetailsIntent.putExtra("nama", artifact.getTitle());
                        artifactDetailsIntent.putExtra("deskripsi", artifact.getDescription());

                        startActivity(artifactDetailsIntent);
                    }
                });

                loadFromServer(view);
                break;

            case "local":
                db = new DBHelper(view.getContext());

                Cursor c = db.select("artifact_favorites");
                while (c.moveToNext()) {
                    String code = c.getString(c.getColumnIndex("kode_artifak"));
                    String title = c.getString(c.getColumnIndex("nama"));
                    String description = c.getString(c.getColumnIndex("deskripsi"));
                    Artifact artifact = new Artifact(code, title, description);
                    localArtifacts.add(artifact);
                }

                artifactsAdapter = new ArtifactsAdapter(view.getContext(), localArtifacts);
                artifactsAdapter.notifyDataSetChanged();
                artifactsListView.setAdapter(artifactsAdapter);

                // attach artifact item click listener
                artifactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        Intent artifactDetailsIntent = new Intent(view.getContext(), ArtifactDetailsActivity.class);
                        Artifact artifact = (Artifact) artifactsListView.getItemAtPosition(position);

                        artifactDetailsIntent.putExtra("kode_artifak", artifact.getCode());
                        artifactDetailsIntent.putExtra("nama", artifact.getTitle());
                        artifactDetailsIntent.putExtra("deskripsi", artifact.getDescription());
                        artifactDetailsIntent.putExtra("local", true);
                        startActivity(artifactDetailsIntent);
                    }
                });

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
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void ShowDialog(String msg, boolean check) {
        if (check) {
            dialog.setMessage(msg);
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }

    private void loadFromServer(final View view) {
        String requestUrl = mEndpoint + "artifak/get-artifak";
        Cache cache = AppSingleton.getInstance(view.getContext()).getRequestQueue().getCache();
        Cache.Entry entry = cache.get(requestUrl);
        if (entry != null) {
            try {
                JSONObject response = new JSONObject(new String(entry.data, "UTF-8"));
                JSONArray artifactList = response.getJSONArray("data");
                for (int i = 0; i < artifactList.length(); i++) {
                    JSONObject artifact = artifactList.getJSONObject(i);
                    remoteArtifacts.add(new Artifact(artifact.getString("kode_artifak"), artifact.getString("nama"), artifact.getString("deskripsi")));
                }
                artifactsAdapter = new ArtifactsAdapter(view.getContext(), remoteArtifacts);
                artifactsAdapter.notifyDataSetChanged();
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
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            ShowDialog("Loading..", true);
            JsonObjectRequest getArtifactList = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ShowDialog("", false);
                            try {
                                db = new DBHelper(view.getContext());
                                db.delete("artifact", "1=1");

                                JSONArray artifactList = response.getJSONArray("data");
                                for (int i = 0; i < artifactList.length(); i++) {
                                    JSONObject artifact = artifactList.getJSONObject(i);
                                    remoteArtifacts.add(new Artifact(artifact.getString("kode_artifak"), artifact.getString("nama"), artifact.getString("deskripsi")));
                                    Map<String, String> data = new HashMap<>();
                                    data.put("kode_artifak", artifact.getString("kode_artifak"));
                                    data.put("nama", artifact.getString("nama"));
                                    data.put("deskripsi", artifact.getString("deskripsi"));
                                    db.insert("artifact", data);
                                }

                                artifactsAdapter = new ArtifactsAdapter(view.getContext(), remoteArtifacts);
                                artifactsAdapter.notifyDataSetChanged();
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

                                loadFromClient(view);
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

                            loadFromClient(view);
                        }
                    }) {

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // refresh every 3 minutes
                    final long cacheExpired = 24 * 60 * 60 * 1000; // expire in 24 hours
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    try {
                        final String jsonString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers));
                        return Response.success(new JSONObject(jsonString), cacheEntry);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return Response.error(new ParseError(e));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return Response.error(new ParseError(e));
                    }
                }

                @Override
                protected void deliverResponse(JSONObject response) {
                    super.deliverResponse(response);
                }

                @Override
                public void deliverError(VolleyError error) {
                    super.deliverError(error);
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    return super.parseNetworkError(volleyError);
                }
            };

            getArtifactList.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(10),//time out in 10second
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//DEFAULT_MAX_RETRIES = 1;
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppSingleton.getInstance(view.getContext()).addToRequestQueue(getArtifactList, TAG);
        }
    }

    private void loadFromClient(View view) {
        db = new DBHelper(view.getContext());
        Cursor c = db.select("artifact");
        while (c.moveToNext()) {
            String code = c.getString(c.getColumnIndex("kode_artifak"));
            String title = c.getString(c.getColumnIndex("nama"));
            String description = c.getString(c.getColumnIndex("deskripsi"));
            Artifact artifact = new Artifact(code, title, description);
            localArtifacts.add(artifact);
        }

        artifactsAdapter = new ArtifactsAdapter(view.getContext(), localArtifacts);
        artifactsAdapter.notifyDataSetChanged();
        artifactsListView.setAdapter(artifactsAdapter);
    }

}
