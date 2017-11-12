package com.example.acer.monperabook;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.acer.monperabook.AsyncTask.DownloadImageTask;
import com.example.acer.monperabook.SQLite.DBHelper;
import com.example.acer.monperabook.Singleton.AppSingleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Azhary Arliansyah on 29/10/2017.
 */

public class ArtifactDetailsActivity extends AppCompatActivity {

    private Bundle extras;
    private TextView title;
    private TextView description;
    private ImageView thumbnail;
    private RatingBar ratingBar;
    private Button rateBtn;
    private Button favoriteBtn;
    private Context mContext;
    private String mEndpoint;
    private String code;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_artifact_details);

        extras          = getIntent().getExtras();
        code            = extras.getString("kode_artifak");
        thumbnail       = (ImageView)findViewById(R.id.thumbnail);
        title           = (TextView)findViewById(R.id.titles);
        description     = (TextView)findViewById(R.id.description);
        ratingBar       = (RatingBar)findViewById(R.id.ratingBar);
        rateBtn         = (Button)findViewById(R.id.rateBtn);
        favoriteBtn     = (Button)findViewById(R.id.favoriteBtn);
        mContext        = ArtifactDetailsActivity.this;
        mEndpoint       = getString(R.string.server_ip);
        db              = new DBHelper(mContext);

        String imgUrl   = mEndpoint + "img/" + code + ".jpg";
        new DownloadImageTask(thumbnail).execute(imgUrl);

        title.setText(extras.getString("nama"));
        description.setText(extras.getString("deskripsi"));

        if (extras.containsKey("local")) {
            ratingBar.setVisibility(View.GONE);
            rateBtn.setVisibility(View.GONE);
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

            rateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String URL = mEndpoint + "artifak/rate-artifak";
                    final String TAG = "RATE";
                    StringRequest rateArtifact = new StringRequest(Request.Method.POST, URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject res = new JSONObject(response);
                                        boolean error = res.getBoolean("error");
                                        if (!error) {
                                            ratingBar.setIsIndicator(true);
                                            Toast.makeText(mContext, "Berhasil", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(mContext, res.getString("error_message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        String msg = e.getMessage();
                                        if (msg != null) {
                                            Log.e(TAG, msg);
                                        }
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
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("kode_artifak", code);
                            params.put("rate", String.valueOf(ratingBar.getRating()));
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("kode_artifak", code);
                            params.put("rate", String.valueOf(ratingBar.getRating()));
                            return params;
                        }
                    };

                    AppSingleton.getInstance(mContext).addToRequestQueue(rateArtifact, TAG);
                }
            });
        }
    }

}
