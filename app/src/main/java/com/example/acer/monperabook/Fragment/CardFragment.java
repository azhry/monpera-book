package com.example.acer.monperabook.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.acer.monperabook.CustomAdapter.CardAdapter;
import com.example.acer.monperabook.R;

/**
 * Created by Azhary Arliansyah on 10/02/2018.
 */

public class CardFragment extends Fragment {

    private CardView mCardView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_adapter, container, false);
        mCardView = (CardView) view.findViewById(R.id.cardView);
        mCardView.setMaxCardElevation(mCardView.getCardElevation()
                * CardAdapter.MAX_ELEVATION_FACTOR);
        CardView.LayoutParams layoutParams = (CardView.LayoutParams) mCardView.getLayoutParams();
        layoutParams.height = 100;
        return view;
    }

    public CardView getCardView() {
        return mCardView;
    }
}
