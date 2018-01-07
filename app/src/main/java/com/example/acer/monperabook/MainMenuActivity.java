package com.example.acer.monperabook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.acer.monperabook.CustomAdapter.MainMenuAdapter;

/**
 * Created by Azhary Arliansyah on 04/01/2018.
 */

public class MainMenuActivity extends AppCompatActivity {

    public static String TAG = "PROJECT.MainMenuActivity";
    private static int SPAN_COUNT = 2;
    private static String[] MENUS = new String[] {
        "Berdasarkan Jenis",
            "Profil Museum",
            "Populer",
            "Tutorial/Help",
            "Tentang Aplikasi",
            "Cari"
    };
    private static int[] ICONS = {
            R.drawable.ic_subject_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_favorite_black,
            R.drawable.ic_help_outline_black_24dp,
            R.drawable.ic_error_outline_black_24dp,
            R.drawable.ic_search_black_24dp
    };
    private Intent[] ACTIVITIES = {
            new Intent(MainMenuActivity.this, MainActivity.class),
            new Intent(MainMenuActivity.this, MainActivity.class),
            new Intent(MainMenuActivity.this, MainActivity.class),
            new Intent(MainMenuActivity.this, MainActivity.class),
            new Intent(MainMenuActivity.this, MainActivity.class),
            new Intent(MainMenuActivity.this, MainActivity.class)
    };

    private Context context;
    private RecyclerView menuRecyclerView;
    private MainMenuAdapter menuRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        context = getApplicationContext();
        menuRecyclerView = (RecyclerView)findViewById(R.id.menu_recycler_view);
        menuRecyclerView.setLayoutManager(new GridLayoutManager(context, SPAN_COUNT));
        menuRecyclerViewAdapter = new MainMenuAdapter(context, MENUS, ICONS, ACTIVITIES);
        menuRecyclerView.setAdapter(menuRecyclerViewAdapter);
    }
}
