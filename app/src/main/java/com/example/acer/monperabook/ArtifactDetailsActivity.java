package com.example.acer.monperabook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Azhary Arliansyah on 29/10/2017.
 */

public class ArtifactDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_artifact_details);

        Bundle extras           = getIntent().getExtras();
        TextView title          = (TextView)findViewById(R.id.titles);
        TextView description    = (TextView)findViewById(R.id.description);

        title.setText(extras.getString("nama"));
        description.setText(extras.getString("deskripsi"));
    }

}
