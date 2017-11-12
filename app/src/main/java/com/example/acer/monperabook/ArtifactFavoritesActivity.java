package com.example.acer.monperabook;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.acer.monperabook.CustomAdapter.Artifact;
import com.example.acer.monperabook.CustomAdapter.ArtifactsAdapter;
import com.example.acer.monperabook.SQLite.DBHelper;

import java.util.ArrayList;

/**
 * Created by Azhary Arliansyah on 12/11/2017.
 */

public class ArtifactFavoritesActivity extends AppCompatActivity {

    private Context mContext;
    private DBHelper db;
    private EditText searchEditText;
    private ListView artifactsListView;
    private ArrayList<Artifact> artifacts = new ArrayList<>();
    private ArtifactsAdapter artifactsAdapter;
    private BottomNavigationView mBottomNav;
    private String TAG = "monperabook.artifact_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artifact_favorites);

        mContext = this;
        db = new DBHelper(mContext);

        searchEditText = (EditText) findViewById(R.id.search);

        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });

        artifactsListView = (ListView) findViewById(R.id.list);

        // attach artifact item click listener
        artifactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent artifactDetailsIntent = new Intent(mContext, ArtifactDetailsActivity.class);
                Artifact artifact = (Artifact) artifactsListView.getItemAtPosition(position);

                artifactDetailsIntent.putExtra("kode_artifak", artifact.getCode());
                artifactDetailsIntent.putExtra("nama", artifact.getTitle());
                artifactDetailsIntent.putExtra("deskripsi", artifact.getDescription());
                artifactDetailsIntent.putExtra("local", true);

                startActivity(artifactDetailsIntent);
            }
        });

        Cursor c = db.select("artifact_favorites");
        while (c.moveToNext()) {
            String code = c.getString(c.getColumnIndex("kode_artifak"));
            String title = c.getString(c.getColumnIndex("nama"));
            String description = c.getString(c.getColumnIndex("deskripsi"));
            Artifact artifact = new Artifact(code, title, description);
            artifacts.add(artifact);
        }

        artifactsAdapter = new ArtifactsAdapter(mContext, artifacts);
        artifactsListView.setAdapter(artifactsAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = searchEditText.getText().toString();
                artifactsAdapter.filter(query);
            }
        });
    }

}
