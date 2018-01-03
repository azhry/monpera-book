package com.example.acer.monperabook.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.acer.monperabook.MainActivity;
import com.example.acer.monperabook.R;
import com.example.acer.monperabook.SQLite.DBHelper;
import com.example.acer.monperabook.SQLite.SessionManager;
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

    public static String TAG = "PROJECT.MenuFragment";

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
    private DBHelper db;

    private static String type;

    private boolean isSearchOpened = false;
    private EditText editSearch;

    public static Fragment newInstance(String type) {
        Fragment frag = new MenuFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        frag.setArguments(args);
        MenuFragment.type = type;
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // to get the action_search edit, override this method
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        final MenuItem actionSearch = menu.findItem(R.id.action_search);
        final MenuItem actionLogout = menu.findItem(R.id.action_logout);

        Drawable actionSearchIcon = actionSearch.getIcon();
        actionSearchIcon.mutate().setColorFilter
                (Color.argb(255, 255, 255, 255), PorterDuff.Mode.SRC_IN);

        Drawable actionLogoutIcon = actionLogout.getIcon();
        actionLogoutIcon.mutate().setColorFilter
                (Color.argb(255, 255, 255, 255), PorterDuff.Mode.SRC_IN);
    }

    // to handle the click in the search and logout button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                handleMenuSearch(item);
                return true;

            case R.id.action_logout:
                SessionManager session = new SessionManager(getActivity().getApplicationContext());
                session.logoutUser();
                getActivity().finish();
                return true;
        }
        return false;
    }

    public void onBackPressed(Fragment frag) {
        if (isSearchOpened) {
            final ActionBar action = ((AppCompatActivity)getActivity()).getSupportActionBar(); // get the action bar

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

            MenuItem item = (MenuItem)getActivity().findViewById(R.id.action_search);

            //add the search icon in the action bar
            item.setIcon(getResources().getDrawable(R.drawable.ic_search_black_24dp));
            item.getIcon().mutate().setColorFilter
                    (Color.argb(255, 255, 255, 255), PorterDuff.Mode.SRC_IN);
            isSearchOpened = false;
        }
    }

    protected void handleMenuSearch(final MenuItem item) {
        final ActionBar action = ((AppCompatActivity)getActivity()).getSupportActionBar(); // get the action bar

        if (isSearchOpened) { // test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

            //add the search icon in the action bar
            item.setIcon(getResources().getDrawable(R.drawable.ic_search_black_24dp));
            item.getIcon().mutate().setColorFilter
                    (Color.argb(255, 255, 255, 255), PorterDuff.Mode.SRC_IN);
            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            editSearch = (EditText)action.getCustomView().findViewById(R.id.edit_search); //the text editor
            editSearch.setTextColor(getResources().getColor(R.color.colorActionBarContent));

            editSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    String query = editSearch.getText().toString();
                    artifactsAdapter.filter(query);
                }
            });

            //this is a listener to do a search when the user clicks on search button
            editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
                        action.setDisplayShowTitleEnabled(true); //show the title in the action bar

                        //hides the keyboard
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                        //add the search icon in the action bar
                        item.setIcon(getResources().getDrawable(R.drawable.ic_search_black_24dp));
                        item.getIcon().mutate().setColorFilter
                                (Color.argb(255, 255, 255, 255), PorterDuff.Mode.SRC_IN);
                        isSearchOpened = false;
                        return true;
                    }
                    return false;
                }
            });

            editSearch.requestFocus();

            //open the keyboard focused in the editSearch
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editSearch, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            item.setIcon(getResources().getDrawable(R.drawable.ic_close_black_24dp));
            item.getIcon().mutate().setColorFilter
                    (Color.argb(255, 255, 255, 255), PorterDuff.Mode.SRC_IN);

            isSearchOpened = true;
        }
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
        if (entry != null && !entry.refreshNeeded()) {
            try {
                JSONObject response = new JSONObject(new String(entry.data, "UTF-8"));
                JSONArray artifactList = response.getJSONArray("data");
                for (int i = 0; i < artifactList.length(); i++) {
                    JSONObject artifact = artifactList.getJSONObject(i);
                    remoteArtifacts.add(new Artifact(artifact.getString("kode_artifak"),
                            artifact.getString("nama"), artifact.getString("deskripsi"),
                            artifact.getString("like")));
                }
                artifactsAdapter = new ArtifactsAdapter(view.getContext(), remoteArtifacts);
                artifactsAdapter.notifyDataSetChanged();
                artifactsListView.setAdapter(artifactsAdapter);

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
                                remoteArtifacts.clear();

                                JSONArray artifactList = response.getJSONArray("data");
                                for (int i = 0; i < artifactList.length(); i++) {
                                    JSONObject artifact = artifactList.getJSONObject(i);
                                    remoteArtifacts.add(new Artifact(artifact.getString("kode_artifak"),
                                            artifact.getString("nama"), artifact.getString("deskripsi"),
                                            artifact.getString("like")));
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
