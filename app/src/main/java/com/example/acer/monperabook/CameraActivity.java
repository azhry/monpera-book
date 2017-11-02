package com.example.acer.monperabook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.monperabook.Singleton.AppSingleton;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Azhary Arliansyah on 29/10/2017.
 */

public class CameraActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private String mEndpoint;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        mEndpoint = getString(R.string.server_ip);
        mContext = this;
    }

    @Override
    public void handleResult(Result result) {
        final String TAG = "QR_CODE";
        String artifactCode = result.getText();
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
                                    mScannerView.stopCamera();
                                    Intent artifactDetailsIntent = new Intent(mContext, ArtifactDetailsActivity.class);
                                    artifactDetailsIntent.putExtra("kode_artifak", artifact.getString("kode_artifak"));
                                    artifactDetailsIntent.putExtra("nama", artifact.getString("nama"));
                                    artifactDetailsIntent.putExtra("deskripsi", artifact.getString("deskripsi"));
                                    startActivity(artifactDetailsIntent);
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
}
