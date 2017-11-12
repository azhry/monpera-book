package com.example.acer.monperabook;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.acer.monperabook.Fragment.MenuFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;
    private Context mContext;
    private int mSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt("item_id", 0);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
        } else {
            selectedItem = mBottomNav.getMenu().getItem(0);
        }
        selectFragment(selectedItem);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("item_id", mSelectedItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        MenuItem homeItem = mBottomNav.getMenu().getItem(0);
        if (mSelectedItem != homeItem.getItemId()) {
            selectFragment(homeItem);
        } else {
            super.onBackPressed();
        }
    }

    private void selectFragment(MenuItem item) {
        Fragment frag = null;
        switch (item.getItemId()) {
            case R.id.menu_home:
                frag = MenuFragment.newInstance("remote");
                break;
            case R.id.menu_favorite:
                frag = MenuFragment.newInstance("local");
                break;
        }

        mSelectedItem = item.getItemId();

        if (mSelectedItem == R.id.menu_scan) {
            Intent scanQRIntent = new Intent(mContext, CameraActivity.class);
            startActivity(scanQRIntent);
        } else {
            for (int i = 0; i < mBottomNav.getMenu().size(); i++) {
                MenuItem menuItem = mBottomNav.getMenu().getItem(i);
                menuItem.setChecked(menuItem.getItemId() == item.getItemId());
            }

            if (frag != null) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container, frag, frag.getTag());
                ft.commit();
            }
        }
    }
}
