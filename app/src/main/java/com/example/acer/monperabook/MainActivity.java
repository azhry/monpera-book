package com.example.acer.monperabook;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.acer.monperabook.Fragment.MenuFragment;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "PROJECT.MainActivity";
    public static int REQUEST_IMAGE_CAPTURE = 2;

    private BottomNavigationView mBottomNav;
    private Context mContext;
    private int mSelectedItem;
    private Toolbar mToolbar;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentFragment = null;
        mContext = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorActionBarContent));

        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        disableShiftMode(mBottomNav);
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
        if (currentFragment != null) {
            MenuFragment f = new MenuFragment();
            f.onBackPressed(currentFragment);
        }

        MenuItem homeItem = mBottomNav.getMenu().getItem(0);
        if (mSelectedItem != homeItem.getItemId()) {
            selectFragment(homeItem);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap img = (Bitmap)data.getExtras().get("data");
            Intent imageCaptureIntent = new Intent(mContext, ImageCaptureActivity.class);
            imageCaptureIntent.putExtra("result", img);
            startActivity(imageCaptureIntent);
        }
    }

    private void selectFragment(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                mBottomNav.getMenu().getItem(0).setChecked(true);
                currentFragment = MenuFragment.newInstance("remote");
                break;
            case R.id.menu_favorite:
                mBottomNav.getMenu().getItem(1).setChecked(true);
                currentFragment = MenuFragment.newInstance("local");
                break;
        }

        mSelectedItem = item.getItemId();

        if (mSelectedItem == R.id.menu_scan) {
            Intent scanQRIntent = new Intent(mContext, CameraActivity.class);
            startActivity(scanQRIntent);
        } else if (mSelectedItem == R.id.menu_selfie) {
//            Intent selfieIntent = new Intent(mContext, ImageCaptureActivity.class);
//            startActivity(selfieIntent);
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            mSelectedItem = R.id.menu_home;
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            if (currentFragment != null) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container, currentFragment, currentFragment.getTag());
                ft.commit();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }
}
