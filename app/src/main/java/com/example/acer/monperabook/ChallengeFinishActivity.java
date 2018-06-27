package com.example.acer.monperabook;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Azhary Arliansyah on 03/03/2018.
 */

public class ChallengeFinishActivity extends AppCompatActivity {

    public static int REQUEST_IMAGE_CAPTURE = 1;
    public static int REQUEST_PERMISSIONS = 2;

    private Toolbar toolbar;
    private ImageView refreshButton;
    private ImageView cameraButton;
    private ImageView saveButton;
    private ImageView selfieImage;
    private TextView scoreText;
    private Bitmap generatedImage;
    private Context mContext;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_finish);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorActionBarContent));
        mContext = this;
        extras = getIntent().getExtras();

        refreshButton = (ImageView) findViewById(R.id.refreshButton);
        cameraButton = (ImageView) findViewById(R.id.cameraButton);
        saveButton = (ImageView) findViewById(R.id.saveButton);
        selfieImage = (ImageView) findViewById(R.id.selfieImage);
        scoreText = (TextView) findViewById(R.id.skor);
        scoreText.setText("Skor: " + String.valueOf(extras.getFloat("score")) + "%");

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasPermissions(mContext, permissions))
            ActivityCompat.requestPermissions((Activity)mContext, permissions, REQUEST_PERMISSIONS);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selfieImage.setImageResource(R.drawable.twibbon);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selfieIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (selfieIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(selfieIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveImage();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            generatedImage = (Bitmap)data.getExtras().get("data");
            generatedImage = Bitmap.createScaledBitmap(generatedImage, 200, 200, true);
            Bitmap twibbonImage = BitmapFactory.decodeResource(getResources(), R.drawable.twibbon);
            twibbonImage = Bitmap.createScaledBitmap(twibbonImage, 200, 200, true);
            generatedImage = createSingleImageFromMultipleImages(generatedImage, twibbonImage);
            selfieImage.setImageBitmap(generatedImage);
        }
    }

    private void saveImage() throws FileNotFoundException {
        // TODO: Save image to local storage
        if (generatedImage != null) {
            String sdCard = Environment.getExternalStorageDirectory().toString();
            File dir = new File(sdCard, "SMBMuseum");
            if (!dir.exists()) {
                dir.mkdir();
            }

            String fileName = String.valueOf(System.currentTimeMillis())  + ".jpg";
            File generated = new File(dir.getAbsolutePath(), fileName);
            try {
                FileOutputStream out = new FileOutputStream(generated);
                generatedImage.compress(Bitmap.CompressFormat.JPEG, 95, out);
                out.flush();
                out.close();
                MediaStore.Images.Media.insertImage(getContentResolver(), generated.getAbsolutePath(),
                        generated.getName() , generated.getName());
                Toast.makeText(mContext, "Image saved at " + generated.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(mContext, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage){
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, 0f, 0f, null);
        return result;
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
