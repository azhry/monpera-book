package com.example.acer.monperabook.AsyncTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Azhary Arliansyah on 09/11/2017.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    ImageView img;

    public DownloadImageTask(ImageView img) {
        this.img = img;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String URL = urls[0];
        Bitmap bm = null;
        try {
            InputStream in = new java.net.URL(URL).openStream();
            bm = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
            e.printStackTrace();
        }
        return bm;
    }

    protected void onPostExecute(Bitmap result) {
        img.setImageBitmap(result);
    }
}
