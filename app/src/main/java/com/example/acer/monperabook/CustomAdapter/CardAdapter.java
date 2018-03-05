package com.example.acer.monperabook.CustomAdapter;

import android.support.v7.widget.CardView;

/**
 * Created by Azhary Arliansyah on 10/02/2018.
 */

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
