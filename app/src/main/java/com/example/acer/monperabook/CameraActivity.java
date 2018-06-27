package com.example.acer.monperabook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.monperabook.SQLite.DBHelper;
import com.example.acer.monperabook.Singleton.AppSingleton;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Azhary Arliansyah on 29/10/2017.
 */

public class CameraActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private Toolbar toolbar;
    private TextView codeText;

    private String mEndpoint;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        if (permissionCheck == -1) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }

        mEndpoint = getString(R.string.server_ip);
        mContext = this;

        setContentView(R.layout.activity_camera);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorActionBarContent));

        codeText = (TextView) findViewById(R.id.kode);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        mScannerView = new ZXingScannerView(this);
//        setContentView(mScannerView);
        linearLayout.addView(mScannerView);
    }

    @Override
    public void handleResult(Result result) {
        final String TAG = "QR_CODE";
        String artifactCode = result.getText();
        codeText.setText(artifactCode);
        String requestURL = mEndpoint + "artifak/get-artifak?kode_artifak=" + artifactCode;
        JsonObjectRequest getArtifact = new JsonObjectRequest(Request.Method.GET, requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean error = response.getBoolean("error");
                            if (!error) {
                                JSONArray data = response.getJSONArray("data");
                                if (data.length() > 0) {
                                    JSONObject artifact = data.getJSONObject(0);

                                    // check whether artifact had been scanned before
                                    DBHelper db = new DBHelper(mContext);
                                    Cursor record = db.select("question",
                                            "kode_artifak='" + artifact.getString("kode_artifak") + "'");
                                    if (!record.moveToFirst()) {
                                        Map<String, String> question = new HashMap<>();
                                        question.put("kode_artifak", artifact.getString("kode_artifak"));
                                        question.put("pertanyaan", "");
                                        db.insert("question", question);
                                    }

                                    Intent artifactDetailsIntent = new Intent(mContext, ArtifactDetailsActivity.class);
                                    artifactDetailsIntent.putExtra("kode_artifak", artifact.getString("kode_artifak"));
                                    artifactDetailsIntent.putExtra("nama", artifact.getString("nama"));
                                    artifactDetailsIntent.putExtra("deskripsi", artifact.getString("deskripsi"));
                                    artifactDetailsIntent.putExtra("foto", artifact.getString("foto"));
                                    artifactDetailsIntent.putExtra("like", artifact.getString("like"));
                                    artifactDetailsIntent.putExtra("kategori", artifact.getString("kategori"));
                                    mScannerView.stopCamera();
                                    startActivity(artifactDetailsIntent);
                                    finish();
                                } else {
                                    Toast.makeText(mContext, "Data not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(mContext, "Server error", Toast.LENGTH_SHORT).show();
                            }

                            mScannerView.resumeCameraPreview((ZXingScannerView.ResultHandler) mContext);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mScannerView.resumeCameraPreview((ZXingScannerView.ResultHandler) mContext);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = error.getMessage();
                        if (msg != null) {
                            Log.e(TAG, msg);
                        } else {
                            Log.e(TAG, "Unknown error");
                        }

                        mScannerView.resumeCameraPreview((ZXingScannerView.ResultHandler) mContext);
                    }
                }
        );

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(getArtifact, TAG);

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    private void getQuestion() {



    }
}
