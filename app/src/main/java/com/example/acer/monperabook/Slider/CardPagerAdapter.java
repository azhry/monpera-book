package com.example.acer.monperabook.Slider;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.acer.monperabook.ArtifactDetailsActivity;
import com.example.acer.monperabook.CustomAdapter.CardAdapter;
import com.example.acer.monperabook.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azhary Arliansyah on 10/02/2018.
 */

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private static String TAG = "CardPagerAdapter";

    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;
    private Context context;

    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public CardPagerAdapter(Context context) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        this.context = context;
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        Log.e(TAG, "getCardViewAt");
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View v = LayoutInflater.from(container.getContext())
                .inflate(R.layout.item_viewpager, container, false);
        container.addView(v);
        bind(mData.get(position), v, position);
        CardView cardView = (CardView) v.findViewById(R.id.cardView);
//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("POSITION", String.valueOf(position));
//                Intent artifactDetailsIntent = new Intent(v.getContext(), ArtifactDetailsActivity.class);
//                artifactDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                CardItem item = (CardItem) mData.get(position);
//
//                artifactDetailsIntent.putExtra("kode_artifak", item.getCode());
//                artifactDetailsIntent.putExtra("nama", item.getTitle());
//                artifactDetailsIntent.putExtra("deskripsi", item.getContent());
//                artifactDetailsIntent.putExtra("foto", item.getImgUrl());
//
//                v.getContext().startActivity(artifactDetailsIntent);
//            }
//        });

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return v;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(final CardItem item, View view, final int position) {
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        titleTextView.setText(item.getTitle());
        Glide.with(view)
                .load(item.getImgUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        return false;
                    }
                })
                .into(thumbnail);
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent artifactDetailsIntent = new Intent(view.getContext(), ArtifactDetailsActivity.class);
                artifactDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                CardItem item = (CardItem) mData.get(position);

                artifactDetailsIntent.putExtra("kode_artifak", item.getCode());
                artifactDetailsIntent.putExtra("nama", item.getTitle());
                artifactDetailsIntent.putExtra("deskripsi", item.getContent());
                artifactDetailsIntent.putExtra("foto", item.getImgUrl());

                view.getContext().startActivity(artifactDetailsIntent);
            }
        });
    }

}
