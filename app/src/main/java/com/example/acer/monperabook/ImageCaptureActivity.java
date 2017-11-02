package com.example.acer.monperabook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Azhary Arliansyah on 02/11/2017.
 */

public class ImageCaptureActivity extends AppCompatActivity {

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

        img = (ImageView)findViewById(R.id.mergedPhoto);
    }

    public void buttonMerge(View view) {
        Bitmap bigImage = BitmapFactory.decodeResource(getResources(), R.drawable.p200x200);
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
