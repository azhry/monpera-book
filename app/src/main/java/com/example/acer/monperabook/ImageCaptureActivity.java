package com.example.acer.monperabook;

import android.*;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Azhary Arliansyah on 02/11/2017.
 */

public class ImageCaptureActivity extends AppCompatActivity {

    public static String TAG = "PROJECT.ImageCaptureActivity";
    public static final String INSTAGRAM_SHARE_TITLE = "Share to";
    public static final int COMPRESS_QUALITY = 100;
    public static final int REQUEST_WRITE_STORAGE = 112;

    private Toolbar mToolbar;
    private Button shareBtn;
    private Button saveBtn;
    private ImageView img;
    private Bitmap imgResult;
    private Bitmap mergedImages;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

        mContext = this;

        boolean hasPermission = (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorActionBarContent));
        mToolbar.getNavigationIcon().mutate().setColorFilter
                (Color.argb(255, 255, 255, 255), PorterDuff.Mode.SRC_IN);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveBtn = (Button)findViewById(R.id.save_btn);
        shareBtn = (Button)findViewById(R.id.share_btn);
        img = (ImageView)findViewById(R.id.mergedPhoto);
        imgResult = (Bitmap) getIntent().getExtras().get("result");

        Bitmap capturedImage = imgResult;
        capturedImage = Bitmap.createScaledBitmap(capturedImage, 200, 200, false);
        Bitmap badgeImage = BitmapFactory.decodeResource(getResources(), R.drawable.badge);
        badgeImage = Bitmap.createScaledBitmap(badgeImage, 200, 200, false);

        mergedImages = createSingleImageFromMultipleImages(capturedImage, badgeImage);
        img.setImageBitmap(mergedImages);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToInstagram();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permission granted!");
            } else {
                Toast.makeText(mContext,
                        "The app was not allowed to write to your storage. Hence, it cannot function properly." +
                                " Please consider granting it this permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImage() {
        // TODO: Save image to local storage
        File sdCard = Environment.getExternalStorageDirectory();
        String path = sdCard.getAbsolutePath() + "/SMBMuseum";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        MediaStore.Images.Media.insertImage(getContentResolver(), mergedImages,
                String.valueOf(System.currentTimeMillis()) , "");

        Toast.makeText(mContext, "Image saved!", Toast.LENGTH_SHORT).show();
    }

    private void shareToInstagram() {
        // TODO: Share image to instagram
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");

        Uri uri = null;
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream outStream = new FileOutputStream(file);
            mergedImages.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, outStream);
            uri = Uri.fromFile(file);
            outStream.flush();
            outStream.close();

            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setPackage("com.instagram.android");

            startActivity(Intent.createChooser(share, INSTAGRAM_SHARE_TITLE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "Instagram is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage){
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, 0f, 0f, null);
        return result;
    }
}
