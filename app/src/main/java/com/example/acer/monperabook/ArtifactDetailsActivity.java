package com.example.acer.monperabook;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.example.acer.monperabook.Singleton.AppSingleton;

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

    private Bundle extras;
    private TextView title;
    private TextView description;
    private ImageView thumbnail;
    private ProgressBar thumbnailProgressBar;
    private RatingBar ratingBar;
    private Button rateBtn;
    private Button favoriteBtn;
    private Button shareToInstagramBtn;
    private Context mContext;
    private String mEndpoint;
    private String code;
    private DBHelper db;

    private SliderPagerAdapter mAdapter;
    private SliderIndicator mIndicator;

    private SliderView sliderView;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_artifact_details);

        sliderView = (SliderView)findViewById(R.id.sliderView);
        mLinearLayout = (LinearLayout)findViewById(R.id.pagesContainer);
        setupSlider();

        extras                  = getIntent().getExtras();
        code                    = extras.getString("kode_artifak");
        thumbnail               = (ImageView)findViewById(R.id.thumbnail);
        thumbnailProgressBar    = (ProgressBar)findViewById(R.id.progress);
        title                   = (TextView)findViewById(R.id.titles);
        description             = (TextView)findViewById(R.id.description);
        ratingBar               = (RatingBar)findViewById(R.id.ratingBar);
//        rateBtn                 = (Button)findViewById(R.id.rateBtn);
        shareToInstagramBtn     = (Button)findViewById(R.id.shareToInstagramBtn);
        favoriteBtn             = (Button)findViewById(R.id.favoriteBtn);
        mContext                = ArtifactDetailsActivity.this;
        mEndpoint               = getString(R.string.server_ip);
        db                      = new DBHelper(mContext);

        String imgUrl   = mEndpoint + "img/" + code + ".jpg";
        Glide.with(mContext)
                .load(imgUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        thumbnailProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        thumbnailProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(thumbnail);
//        new DownloadImageTask(thumbnail).execute(imgUrl);

        title.setText(extras.getString("nama"));
        description.setText(extras.getString("deskripsi"));

        if (extras.containsKey("local")) {
            ratingBar.setVisibility(View.GONE);
            //rateBtn.setVisibility(View.GONE);
            favoriteBtn.setVisibility(View.GONE);
        } else {
            favoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor c = db.select("artifact_favorites", "kode_artifak='" + code + "'");
                    Map<String, String> data = new HashMap<>();
                    data.put("kode_artifak", code);
                    data.put("nama", extras.getString("nama"));
                    data.put("deskripsi", extras.getString("deskripsi"));
                    if (c.moveToFirst()) {
                        db.update("artifact_favorites", data, "kode_artifak='" + code + "'");
                    } else {
                        db.insert("artifact_favorites", data);
                    }
                }
            });

            shareToInstagramBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String type = "image/*";
                    createInstagramIntent(type);
                }
            });

//            rateBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String URL = mEndpoint + "artifak/rate-artifak";
//                    final String TAG = "RATE";
//                    StringRequest rateArtifact = new StringRequest(Request.Method.POST, URL,
//                            new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    try {
//                                        JSONObject res = new JSONObject(response);
//                                        boolean error = res.getBoolean("error");
//                                        if (!error) {
//                                            ratingBar.setIsIndicator(true);
//                                            Toast.makeText(mContext, "Berhasil", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Toast.makeText(mContext, res.getString("error_message"), Toast.LENGTH_SHORT).show();
//                                        }
//                                    } catch (JSONException e) {
//                                        String msg = e.getMessage();
//                                        if (msg != null) {
//                                            Log.e(TAG, msg);
//                                        }
//                                        e.printStackTrace();
//                                    }
//                                }
//                            },
//                            new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    String msg = error.getMessage();
//                                    if (msg != null) {
//                                        Log.e(TAG, msg);
//                                    }
//                                }
//                            }
//                    ) {
//                        @Override
//                        protected Map<String, String> getParams() {
//                            Map<String, String> params = new HashMap<>();
//                            params.put("kode_artifak", code);
//                            params.put("rate", String.valueOf(ratingBar.getRating()));
//                            return params;
//                        }
//
//                        @Override
//                        public Map<String, String> getHeaders() throws AuthFailureError {
//                            Map<String, String> params = new HashMap<>();
//                            params.put("kode_artifak", code);
//                            params.put("rate", String.valueOf(ratingBar.getRating()));
//                            return params;
//                        }
//                    };
//
//                    AppSingleton.getInstance(mContext).addToRequestQueue(rateArtifact, TAG);
//                }
//            });
        }
    }

    private void createInstagramIntent(String type){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        thumbnail.buildDrawingCache();
        Bitmap bmp = thumbnail.getDrawingCache();
        Uri uri = getLocalBitmapUri(bmp);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        share.setPackage("com.instagram.android");

        try {
            // Broadcast the Intent.
            startActivity(Intent.createChooser(share, "Share to"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "Instagram is not installed", Toast.LENGTH_SHORT).show();
        }
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
        fragments.add(FragmentSlider.newInstance("https://image.tmdb.org/t/p/w250_and_h141_bestv2/biN2sqExViEh8IYSJrXlNKjpjxx.jpg"));
        fragments.add(FragmentSlider.newInstance("https://image.tmdb.org/t/p/w250_and_h141_bestv2/o9OKe3M06QMLOzTl3l6GStYtnE9.jpg"));

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

}
