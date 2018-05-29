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
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.StringRequest;
import com.example.acer.monperabook.AboutAppActivity;
import com.example.acer.monperabook.ArtifactDetailsActivity;
import com.example.acer.monperabook.ChallengeActivity;
import com.example.acer.monperabook.CustomAdapter.Artifact;
import com.example.acer.monperabook.CustomAdapter.ArtifactsAdapter;
import com.example.acer.monperabook.CustomAdapter.CollectionsRecyclerAdapter;
import com.example.acer.monperabook.CustomAdapter.MainMenuAdapter;
import com.example.acer.monperabook.ImageSlider.FragmentSlider;
import com.example.acer.monperabook.ImageSlider.SliderIndicator;
import com.example.acer.monperabook.ImageSlider.SliderPagerAdapter;
import com.example.acer.monperabook.ImageSlider.SliderView;
import com.example.acer.monperabook.MainActivity;
import com.example.acer.monperabook.MuseumProfileActivity;
import com.example.acer.monperabook.R;
import com.example.acer.monperabook.SQLite.DBHelper;
import com.example.acer.monperabook.SQLite.SessionManager;
import com.example.acer.monperabook.SearchActivity;
import com.example.acer.monperabook.Singleton.AppSingleton;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Azhary Arliansyah on 12/11/2017.
 */

public class MenuFragment extends Fragment {

    public static String TAG = "PROJECT.MenuFragment";
    private ListView artifactsListView;
    private ArrayList<Artifact> remoteArtifacts = new ArrayList<>();
    private ArrayList<Artifact> localArtifacts = new ArrayList<>();
    private ArrayList<Artifact> popularCollections = new ArrayList<>();
    private ArtifactsAdapter artifactsAdapter;
    private CollectionsRecyclerAdapter popularCollectionsRecyclerAdapter;
    private RecyclerView popularCollectionRecyclerView;
    private ProgressDialog dialog;
    private String mEndpoint;
    private DBHelper db;
    private static String type;
    private SliderPagerAdapter mAdapter;
    private SliderIndicator mIndicator;
    private SliderView sliderView;
    private LinearLayout mLinearLayout;
    private FragmentActivity fragmentActivity;
    private RecyclerView menuRecyclerView;
    private MainMenuAdapter menuRecyclerViewAdapter;
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
    public void onAttach(Activity activity) {
        fragmentActivity =(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        switch (type) {
            case "main":
                return inflater.inflate(R.layout.activity_main_menu, container, false);
            case "other":
                return inflater.inflate(R.layout.activity_other_menu, container, false);
            case "local":
                return inflater.inflate(R.layout.activity_artifact_favorites, container, false);
            default:
                return inflater.inflate(R.layout.menu_fragment, container, false);
        }

    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new ProgressDialog(view.getContext());
        dialog.setCancelable(false);

        mEndpoint = getString(R.string.server_ip);

        switch (type) {
            case "main":
                int SPAN_COUNT = 2;
                String[] MENUS = new String[] {
//                    "Berdasarkan Jenis",
                    "Profil Museum",
//                    "Populer",
                    "Tutorial/Help",
                    "Tentang Aplikasi",
                    "Cari"
                };
                int[] ICONS = new int[] {
//                    R.drawable.ic_subject_black_24dp,
                    R.drawable.ic_account_balance_black_24dp,
//                    R.drawable.ic_favorite_black,
                    R.drawable.ic_help_outline_black_24dp,
                    R.drawable.ic_error_outline_black_24dp,
                    R.drawable.ic_search_black_24dp
                };
                ArrayList<Intent> ACTIVITIES = new ArrayList<>();
//                ACTIVITIES.add(new Intent(getActivity(), MainActivity.class));
                ACTIVITIES.add(new Intent(getActivity(), MuseumProfileActivity.class));
//                ACTIVITIES.add(new Intent(getActivity(), MainActivity.class));
                ACTIVITIES.add(new Intent(getActivity(), MainActivity.class));
                ACTIVITIES.add(new Intent(getActivity(), AboutAppActivity.class));
                ACTIVITIES.add(new Intent(getActivity(), SearchActivity.class));
                for (Intent intent : ACTIVITIES) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                menuRecyclerView = (RecyclerView)view.findViewById(R.id.menu_recycler_view);
                menuRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), SPAN_COUNT));
                menuRecyclerViewAdapter = new MainMenuAdapter(getApplicationContext(), MENUS, ICONS, ACTIVITIES);
                menuRecyclerView.setAdapter(menuRecyclerViewAdapter);

                break;
            case "other":
                sliderView      = (SliderView)view.findViewById(R.id.sliderView);
                mLinearLayout   = (LinearLayout)view.findViewById(R.id.pagesContainer);
                setupSlider(fragmentActivity.getSupportFragmentManager());

                popularCollectionRecyclerView = (RecyclerView) view.findViewById(R.id.collectionRecyclerView);
                LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(),
                        LinearLayoutManager.HORIZONTAL, false);
                popularCollectionRecyclerView.setLayoutManager(layoutManager);
                popularCollectionRecyclerView.setHasFixedSize(true);
                popularCollectionsRecyclerAdapter = new CollectionsRecyclerAdapter(popularCollections,
                        view.getContext());
                popularCollectionRecyclerView.setAdapter(popularCollectionsRecyclerAdapter);
                getMostFavoriteArtifact2(view);

                Button joinChallengeButton = (Button) view.findViewById(R.id.joinChallengeButton);
                joinChallengeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(view.getContext(), ChallengeActivity.class));
                    }
                });
                break;
            case "remote":

                artifactsListView = (ListView) view.findViewById(R.id.list);

                // attach artifact item click listener
                artifactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        Intent artifactDetailsIntent = new Intent(view.getContext(), ArtifactDetailsActivity.class);
                        Artifact artifact = (Artifact) artifactsListView.getItemAtPosition(position);

                        artifactDetailsIntent.putExtra("kode_artifak", artifact.getCode());
                        artifactDetailsIntent.putExtra("nama", artifact.getTitle());
                        artifactDetailsIntent.putExtra("deskripsi", artifact.getDescription());
                        artifactDetailsIntent.putExtra("foto", artifact.getImages());
                        artifactDetailsIntent.putExtra("like", artifact.getLike());

                        startActivity(artifactDetailsIntent);
                    }
                });

                loadFromServer(view);
                break;

            case "local":

                ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
                ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentActivity.getSupportFragmentManager());

                adapter.addFragment(new MyFavoriteFragment(), "Favorit Saya");
                adapter.addFragment(new VisitorFragment(), "Favorit Pengunjung");
                viewPager.setAdapter(adapter);

                TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(viewPager);

//                artifactsListView = (ListView) view.findViewById(R.id.list);
//                db = new DBHelper(view.getContext());
//
//                Cursor c = db.select("artifact_favorites");
//                while (c.moveToNext()) {
//                    String code = c.getString(c.getColumnIndex("kode_artifak"));
//                    String title = c.getString(c.getColumnIndex("nama"));
//                    String description = c.getString(c.getColumnIndex("deskripsi"));
//                    Artifact artifact = new Artifact(code, title, description);
//                    localArtifacts.add(artifact);
//                }
//
//                artifactsAdapter = new ArtifactsAdapter(view.getContext(), localArtifacts);
//                artifactsAdapter.notifyDataSetChanged();
//                artifactsListView.setAdapter(artifactsAdapter);
//
//                // attach artifact item click listener
//                artifactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView parent, View view, int position, long id) {
//                        Intent artifactDetailsIntent = new Intent(view.getContext(), ArtifactDetailsActivity.class);
//                        Artifact artifact = (Artifact) artifactsListView.getItemAtPosition(position);
//
//                        artifactDetailsIntent.putExtra("kode_artifak", artifact.getCode());
//                        artifactDetailsIntent.putExtra("nama", artifact.getTitle());
//                        artifactDetailsIntent.putExtra("deskripsi", artifact.getDescription());
//                        artifactDetailsIntent.putExtra("local", true);
//                        artifactDetailsIntent.putExtra("foto", artifact.getImages());
//                        startActivity(artifactDetailsIntent);
//                    }
//                });
                break;
        }
    }

    // to get the action_search edit, override this method
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.action_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

//        final MenuItem actionSearch = menu.findItem(R.id.action_search);
//        actionSearch.setVisible(true);
//
//        Drawable actionSearchIcon = actionSearch.getIcon();
//        actionSearchIcon.mutate().setColorFilter
//                (Color.argb(255, 255, 255, 255), PorterDuff.Mode.SRC_IN);

        final MenuItem actionLogout = menu.findItem(R.id.action_logout);

        Drawable actionLogoutIcon = actionLogout.getIcon();
        actionLogoutIcon.mutate().setColorFilter
                (Color.argb(255, 255, 255, 255), PorterDuff.Mode.SRC_IN);

    }

    // to handle the click in the search and logout button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_search:
//                handleMenuSearch(item);
//                return true;

            case R.id.action_logout:
                SessionManager session = new SessionManager(getActivity().getApplicationContext());
                session.logoutUser();
                LoginManager.getInstance().logOut();
                getActivity().finish();
                return true;
        }
        return false;
    }

    public void onBackPressed(android.app.Fragment frag) {
        if (isSearchOpened) {
            final ActionBar action = ((AppCompatActivity)getActivity()).getSupportActionBar(); // get the action bar

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

//            MenuItem item = (MenuItem)getActivity().findViewById(R.id.action_search);

            //add the search icon in the action bar
//            item.setIcon(getResources().getDrawable(R.drawable.ic_search_black_24dp));
//            item.getIcon().mutate().setColorFilter
//                    (Color.argb(255, 255, 255, 255), PorterDuff.Mode.SRC_IN);
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

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<android.support.v4.app.Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
        if (entry != null && !entry.refreshNeeded()) {
            try {
                JSONObject response = new JSONObject(new String(entry.data, "UTF-8"));
                JSONArray artifactList = response.getJSONArray("data");
                for (int i = 0; i < artifactList.length(); i++) {
                    JSONObject artifact = artifactList.getJSONObject(i);
                    remoteArtifacts.add(new Artifact(artifact.getString("kode_artifak"),
                            artifact.getString("nama"), artifact.getString("deskripsi"),
                            artifact.getString("like"), artifact.getString("foto")));
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
                                            artifact.getString("like"), artifact.getString("foto")));
                                    Map<String, String> data = new HashMap<>();
                                    data.put("kode_artifak", artifact.getString("kode_artifak"));
                                    data.put("nama", artifact.getString("nama"));
                                    data.put("deskripsi", artifact.getString("deskripsi"));
                                    data.put("foto", artifact.getString("foto"));
                                    db.insert("artifact", data);
                                }

                                artifactsAdapter = new ArtifactsAdapter(view.getContext(), remoteArtifacts);
                                artifactsAdapter.notifyDataSetChanged();
                                artifactsListView.setAdapter(artifactsAdapter);

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

    private static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    private void setupSlider(FragmentManager fragmentManager) {
        sliderView.setDurationScroll(800);
        List<android.support.v4.app.Fragment> fragments = new ArrayList<>();
        fragments.add(FragmentSlider.newInstance(mEndpoint + "img/MBK-1000.jpg"));

        mAdapter = new SliderPagerAdapter(fragmentManager, fragments);
        sliderView.setAdapter(mAdapter);
        mIndicator = new SliderIndicator(getActivity(), mLinearLayout, sliderView, R.drawable.indicator_circle);
        mIndicator.setPageCount(fragments.size());
        mIndicator.show();

        sliderView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    private void getMostFavoriteArtifact2(final View view) {

        StringRequest getMostFavorite = new StringRequest(Request.Method.GET, mEndpoint + "artifak/most-favorite?limit=3",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e(TAG, response);
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray data = jsonResponse.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject artifact = data.getJSONObject(i);
                                JSONArray thumbnailUrl = new JSONArray(artifact.getString("foto"));
                                String imgUrl = mEndpoint + "img/" + thumbnailUrl.getString(0);
                                popularCollections.add(new Artifact(artifact.getString("kode_artifak"),
                                        artifact.getString("nama"), artifact.getString("deskripsi"),
                                        artifact.getString("like"), artifact.getString("foto"),
                                        artifact.getString("kategori")));
                            }

                            popularCollectionsRecyclerAdapter = new CollectionsRecyclerAdapter(popularCollections,
                                    view.getContext());
                            popularCollectionsRecyclerAdapter.notifyDataSetChanged();
                            popularCollectionRecyclerView.setAdapter(popularCollectionsRecyclerAdapter);

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

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(getMostFavorite, TAG + "listview");
    }
}
