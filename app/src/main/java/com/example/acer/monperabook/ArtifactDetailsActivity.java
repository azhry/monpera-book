package com.example.acer.monperabook;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.acer.monperabook.AsyncTask.DownloadImageTask;
import com.example.acer.monperabook.ImageSlider.FragmentSlider;
import com.example.acer.monperabook.ImageSlider.SliderIndicator;
import com.example.acer.monperabook.ImageSlider.SliderPagerAdapter;
import com.example.acer.monperabook.ImageSlider.SliderView;
import com.example.acer.monperabook.SQLite.DBHelper;
import com.example.acer.monperabook.SQLite.SessionManager;
import com.example.acer.monperabook.Singleton.AppSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Azhary Arliansyah on 29/10/2017.
 */

public class ArtifactDetailsActivity extends AppCompatActivity {

    public static String TAG = "PROJECT.ArtifactDetailsActivity";

    private Bundle extras;
    private TextView title;
    private TextView description;
    private Context mContext;
    private String mEndpoint;
    private String code;
    private String images;
    private DBHelper db;
    private SessionManager sessionManager;

    private BottomNavigationView mBottomNav;

    private SliderPagerAdapter mAdapter;
    private SliderIndicator mIndicator;

    private SliderView sliderView;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_artifact_details);

        sessionManager = new SessionManager(this);
        sliderView = (SliderView)findViewById(R.id.sliderView);
        mLinearLayout = (LinearLayout)findViewById(R.id.pagesContainer);

        extras                  = getIntent().getExtras();
        code                    = extras.getString("kode_artifak");
        images                  = extras.getString("foto");
        title                   = (TextView)findViewById(R.id.titles);
        description             = (TextView)findViewById(R.id.description);
        mBottomNav              = (BottomNavigationView) findViewById(R.id.navigation);
        mContext                = ArtifactDetailsActivity.this;
        mEndpoint               = getString(R.string.server_ip);
        db                      = new DBHelper(mContext);

        mBottomNav.getMenu().getItem(0).setCheckable(false);
        mBottomNav.getMenu().getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String requestUrl = mEndpoint + "artifak/like-artifak";
                StringRequest setLike =  new StringRequest(Request.Method.POST, requestUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                checkLike();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String msg = error.getMessage();
                                if (msg != null) {
                                    Log.e(TAG, msg);
                                }
                                Toast.makeText(mContext, "Unknown error", Toast.LENGTH_SHORT).show();
                            }
                        }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("kode_artifak", extras.getString("kode_artifak"));
                        params.put("id_user", sessionManager.getUserId());
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("kode_artifak", extras.getString("kode_artifak"));
                        params.put("id_user", sessionManager.getUserId());
                        return params;
                    }
                };

                AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(setLike, "SET_LIKE");

                return false;
            }
        });

        checkLike();
        setupSlider();

        title.setText(extras.getString("nama"));
        description.setText(extras.getString("deskripsi"));
    }

    private void createInstagramIntent(String type){

//        // Create the new Intent using the 'Send' action.
//        Intent share = new Intent(Intent.ACTION_SEND);
//
//        // Set the MIME type
//        share.setType(type);
//
//        thumbnail.buildDrawingCache();
//        Bitmap bmp = thumbnail.getDrawingCache();
//        Uri uri = getLocalBitmapUri(bmp);
//
//        // Add the URI to the Intent.
//        share.putExtra(Intent.EXTRA_STREAM, uri);
//
//        share.setPackage("com.instagram.android");
//
//        try {
//            // Broadcast the Intent.
//            startActivity(Intent.createChooser(share, "Share to"));
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(mContext, "Instagram is not installed", Toast.LENGTH_SHORT).show();
//        }
    }

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmpUri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void setupSlider() {
        sliderView.setDurationScroll(800);
        List<Fragment> fragments = new ArrayList<>();
        try {
            JSONArray imageSlider = new JSONArray(images);
            for (int i = 0; i < imageSlider.length(); i++) {
                fragments.add(FragmentSlider.newInstance(mEndpoint + "img/" + imageSlider.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAdapter = new SliderPagerAdapter(getSupportFragmentManager(), fragments);
        sliderView.setAdapter(mAdapter);
        mIndicator = new SliderIndicator(this, mLinearLayout, sliderView, R.drawable.indicator_circle);
        mIndicator.setPageCount(fragments.size());
        mIndicator.show();

        sliderView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    private void checkLike() {
        String requestUrl = mEndpoint + "artifak/check-like";
        StringRequest getLike = new StringRequest(Request.Method.POST, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            boolean liked = res.getBoolean("data");
                            if (liked) mBottomNav.getMenu().getItem(0).setCheckable(true);
                            else mBottomNav.getMenu().getItem(0).setCheckable(false);
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
                        Toast.makeText(mContext, "Unknown error", Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kode_artifak", extras.getString("kode_artifak"));
                params.put("id_user", sessionManager.getUserId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("kode_artifak", extras.getString("kode_artifak"));
                params.put("id_user", sessionManager.getUserId());
                return params;
            }
        };

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(getLike, "GET_LIKE");
    }

}
