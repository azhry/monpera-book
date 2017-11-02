package com.example.acer.monperabook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.acer.monperabook.CustomAdapter.Artifact;
import com.example.acer.monperabook.CustomAdapter.ArtifactsAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView artifactsListView;
    private ArrayList<Artifact> artifacts = new ArrayList<>();
    private ArtifactsAdapter artifactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        artifactsListView = (ListView) findViewById(R.id.list);
        artifacts.add(new Artifact("Benda 1", "Deskripsi 1"));
        artifacts.add(new Artifact("Benda 2", "Deskripsi 2"));
        artifacts.add(new Artifact("Benda 3", "Deskripsi 3"));
        artifacts.add(new Artifact("Benda 4", "Deskripsi 4"));
        artifacts.add(new Artifact("Benda 5", "Deskripsi 5"));
        artifacts.add(new Artifact("Benda 6", "Deskripsi 6"));
        artifactsAdapter = new ArtifactsAdapter(this, artifacts);
        artifactsListView.setAdapter(artifactsAdapter);

    }
}
