package com.example.acer.monperabook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.acer.monperabook.CustomAdapter.MainMenuAdapter;
import com.example.acer.monperabook.SQLite.SessionManager;
import com.facebook.login.LoginManager;

import java.util.ArrayList;

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

    private ArrayList<Intent> ACTIVITIES = new ArrayList<>();


    private Context context;
    private RecyclerView menuRecyclerView;
    private MainMenuAdapter menuRecyclerViewAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ACTIVITIES.add(new Intent(MainMenuActivity.this, MainActivity.class));
        ACTIVITIES.add(new Intent(MainMenuActivity.this, MuseumProfileActivity.class));
        ACTIVITIES.add(new Intent(MainMenuActivity.this, MainActivity.class));
        ACTIVITIES.add(new Intent(MainMenuActivity.this, MainActivity.class));
        ACTIVITIES.add(new Intent(MainMenuActivity.this, MainActivity.class));
        ACTIVITIES.add(new Intent(MainMenuActivity.this, MainActivity.class));
        for (Intent intent : ACTIVITIES) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        context = getApplicationContext();
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getResources().getColor(R.color.colorActionBarContent));
        menuRecyclerView = (RecyclerView)findViewById(R.id.menu_recycler_view);
        menuRecyclerView.setLayoutManager(new GridLayoutManager(context, SPAN_COUNT));
        menuRecyclerViewAdapter = new MainMenuAdapter(context, MENUS, ICONS, ACTIVITIES);
        menuRecyclerView.setAdapter(menuRecyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.action_main_menu, menu);

        final MenuItem actionLogout = menu.findItem(R.id.action_logout);
        Drawable actionLogoutIcon = actionLogout.getIcon();
        actionLogoutIcon.mutate().setColorFilter
                (Color.argb(255, 255, 255, 255), PorterDuff.Mode.SRC_IN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                SessionManager session = new SessionManager(getApplicationContext());
                session.logoutUser();
                LoginManager.getInstance().logOut();
                finish();
                break;
        }

        return true;
    }
}
