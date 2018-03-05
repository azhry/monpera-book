package com.example.acer.monperabook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Azhary Arliansyah on 19/01/2018.
 */

public class MuseumProfileActivity extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;
    private TextView showRouteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_profile);
        context = this;
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorActionBarContent));

        showRouteText = (TextView) findViewById(R.id.showRouteText);
        showRouteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapRouteIntent = new Intent(MuseumProfileActivity.this, MapRouteActivity.class);
                startActivity(mapRouteIntent);
            }
        });

    }

}
