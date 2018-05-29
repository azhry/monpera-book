package com.example.acer.monperabook;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.acer.monperabook.CustomAdapter.CatatanRecyclerAdapter;
import com.example.acer.monperabook.ImageSlider.DetailsSlider;
import com.example.acer.monperabook.ImageSlider.FragmentSlider;
import com.example.acer.monperabook.ImageSlider.SliderIndicator;
import com.example.acer.monperabook.ImageSlider.SliderPagerAdapter;
import com.example.acer.monperabook.ImageSlider.SliderView;
import com.example.acer.monperabook.SQLite.DBHelper;
import com.example.acer.monperabook.SQLite.SessionManager;
import com.example.acer.monperabook.Singleton.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Azhary Arliansyah on 29/10/2017.
 */

public class ArtifactDetailsActivity extends AppCompatActivity {

    public static String TAG = "PROJECT.ArtifactDetailsActivity";
    public static int REQUEST_IMAGE_CAPTURE = 1;

    private Bundle extras;
    private TextView title;
    private TextView category;
    private TextView description;
    private Context mContext;
    private String mEndpoint;
    private String code;
    private String images;
    private String likes;
    private DBHelper db;
    private SessionManager sessionManager;
    private EditText addNoteEditText;
    private TextView showAllNoteText;
    private ImageView favoriteButton;
    private ImageView noteButton;
    private ImageView selfieButton;
    private ImageView shareButton;
    private ImageView addNoteButton;
    private TextView likeCountText;

    private BottomNavigationView mBottomNav;

    private SliderPagerAdapter mAdapter;
    private SliderIndicator mIndicator;

    private SliderView sliderView;
    private LinearLayout mLinearLayout;
    private CatatanRecyclerAdapter catatanRecyclerAdapter;
    private RecyclerView catatanRecyclerView;

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
        likes                   = extras.getString("like");
        title                   = (TextView)findViewById(R.id.titles);
        category                = (TextView)findViewById(R.id.category);
        description             = (TextView)findViewById(R.id.description);
        addNoteEditText         = (EditText)findViewById(R.id.addNoteEditText);
        showAllNoteText         = (TextView)findViewById(R.id.showAllNotes);
        favoriteButton          = (ImageView)findViewById(R.id.favoriteButton);
        noteButton              = (ImageView)findViewById(R.id.noteButton);
        selfieButton            = (ImageView)findViewById(R.id.selfieButton);
        shareButton             = (ImageView)findViewById(R.id.shareButton);
        addNoteButton           = (ImageView)findViewById(R.id.addNoteButton);
        likeCountText           = (TextView)findViewById(R.id.likeCountText);
        catatanRecyclerView     = (RecyclerView)findViewById(R.id.catatanRecyclerView);

        mContext                = ArtifactDetailsActivity.this;
        mEndpoint               = getString(R.string.server_ip);
        db                      = new DBHelper(mContext);

        ArrayList<String> catatan = new ArrayList<>();
        ArrayList<String> dateTime = new ArrayList<>();
        Cursor getNote = db.select("note", "kode_artifak='" + code + "'");
        while(getNote.moveToNext()) {
            catatan.add(getNote.getString(getNote.getColumnIndex("note")));
            dateTime.add(getNote.getString(getNote.getColumnIndex("waktu")));
        }

        catatanRecyclerAdapter  = new CatatanRecyclerAdapter(catatan, dateTime, R.layout.catatan_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        catatanRecyclerView.setLayoutManager(mLayoutManager);
        catatanRecyclerView.setAdapter(catatanRecyclerAdapter);

        likeCountText.setText("Difavoritkan oleh " + likes + " pengunjung");

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInstagramIntent("image/*");
            }
        });

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note = addNoteEditText.getText().toString();
                Map<String, String> data = new HashMap<>();
                data.put("kode_artifak", code);
                data.put("note", note);
                data.put("waktu", getCurrentTime());
                db.insert("note", data);

                showNoteDialog(note);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                addNoteEditText.setText("");
            }
        });

        showAllNoteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor checkNote = db.select("note", "kode_artifak='" + code + "'");
                if (checkNote.moveToFirst()) {
                    String note = checkNote.getString(checkNote.getColumnIndex("note"));
                    showNoteDialog(note);
                } else {
                    showNoteDialog("Belum ada catatan");
                }
            }
        });

        selfieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selfieIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (selfieIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(selfieIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNoteEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(addNoteEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            }
        });

        checkLike();
        setupSlider();

        title.setText(extras.getString("nama"));
        description.setText(extras.getString("deskripsi"));
        category.setText(extras.getString("kategori"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap img = (Bitmap)data.getExtras().get("data");
            Intent imageCaptureIntent = new Intent(ArtifactDetailsActivity.this, ImageCaptureActivity.class);
            imageCaptureIntent.putExtra("result", img);
            startActivity(imageCaptureIntent);
        }
    }

    private void createInstagramIntent(String type){

        // Create the new Intent using the 'Send' action.
        final Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        try {

            JSONArray imgUrls = new JSONArray(images);
            if (imgUrls.length() > 0) {
//                Bitmap bmp = getBitmapFromURL(mEndpoint + "img/" + imgUrls.getString(0));
                Glide
                    .with(mContext)
                    .asBitmap()
                    .load(mEndpoint + "img/" + imgUrls.getString(0))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Bitmap bmp = resource;
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
                    });

            } else {
                Toast.makeText(mContext, "Image not found", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
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

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
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
                            if (liked) favoriteButton.setColorFilter(
                                    Color.argb(255, 255, 0, 0), PorterDuff.Mode.SRC_IN);
                            else favoriteButton.setColorFilter(
                                    Color.argb(255, 0, 0, 0), PorterDuff.Mode.SRC_IN);
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

    private void showNoteDialog(String note) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Catatan");
        alertDialogBuilder
                .setMessage(note)
                .setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm dd-MM-yyyy").format(new Date());
    }

}
