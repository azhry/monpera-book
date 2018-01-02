package com.example.acer.monperabook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Azhary Arliansyah on 02/11/2017.
 */

public class ImageCaptureActivity extends AppCompatActivity implements View.OnClickListener {

    public static String TAG = "PROJECT.ImageCaptureActivity";

    private Toolbar mToolbar;
    private ImageView img;
    private Bitmap imgResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorActionBarContent));

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img = (ImageView)findViewById(R.id.mergedPhoto);
        imgResult = (Bitmap) getIntent().getExtras().get("result");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                saveImage();
                break;

            case R.id.share_btn:
                shareToInstagram();
                break;
        }
    }

    private void saveImage() {
        Bitmap capturedImage = imgResult;
        capturedImage = Bitmap.createScaledBitmap(capturedImage, 200, 200, false);

        // TODO: Save image to local storage

    }

    private void shareToInstagram() {
        // TODO: Share image to instagram
    }

    public void buttonMerge() {
        Bitmap bigImage = imgResult;
        //Bitmap bigImage = BitmapFactory.decodeResource(getResources(), R.drawable.p200x200);
        bigImage = Bitmap.createScaledBitmap(bigImage, 200, 200, false);
        Bitmap smallImage = BitmapFactory.decodeResource(getResources(), R.drawable.badge);
        smallImage = Bitmap.createScaledBitmap(smallImage, 200, 200, false);
        Bitmap mergedImages = createSingleImageFromMultipleImages(bigImage, smallImage);

        img.setImageBitmap(mergedImages);
    }

    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage){
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, 0f, 0f, null);
        return result;
    }
}
