package com.example.acer.monperabook.ImageSlider;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.acer.monperabook.R;

/**
 * Created by Azhary Arliansyah on 29/05/2018.
 */

public class DetailsSlider extends Fragment {

    private static final String ARG_PARAM1 = "params";

    private String imageUrls;

    public DetailsSlider() {}

    public static DetailsSlider newInstance(String params) {
        DetailsSlider fragment = new DetailsSlider();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, params);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imageUrls = getArguments().getString(ARG_PARAM1);
        View view = inflater.inflate(R.layout.fragment_slider_item, container, false);
        ImageView img = (ImageView)view.findViewById(R.id.img);
        final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress);
        Glide.with(getActivity())
                .load(imageUrls)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(img);
        return view;
    }

}
