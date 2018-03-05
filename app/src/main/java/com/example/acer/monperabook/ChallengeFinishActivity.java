package com.example.acer.monperabook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Azhary Arliansyah on 03/03/2018.
 */

public class ChallengeFinishActivity extends AppCompatActivity {

    public static int REQUEST_IMAGE_CAPTURE = 1;

    private Toolbar toolbar;
    private ImageView refreshButton;
    private ImageView cameraButton;
    private ImageView saveButton;
    private ImageView selfieImage;
    private Bitmap generatedImage;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_finish);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorActionBarContent));
        mContext = this;

        refreshButton = (ImageView) findViewById(R.id.refreshButton);
        cameraButton = (ImageView) findViewById(R.id.cameraButton);
        saveButton = (ImageView) findViewById(R.id.saveButton);
        selfieImage = (ImageView) findViewById(R.id.selfieImage);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selfieImage.setImageResource(R.drawable.p200x200);
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
                saveImage();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            generatedImage = (Bitmap)data.getExtras().get("data");
            selfieImage.setImageBitmap(generatedImage);
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

        MediaStore.Images.Media.insertImage(getContentResolver(), generatedImage,
                String.valueOf(System.currentTimeMillis()) , "");

        Toast.makeText(mContext, "Image saved!", Toast.LENGTH_SHORT).show();
    }

}
