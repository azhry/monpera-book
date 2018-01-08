package com.example.acer.monperabook.CustomAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.monperabook.MainActivity;
import com.example.acer.monperabook.R;

import java.util.ArrayList;

/**
 * Created by Azhary Arliansyah on 04/01/2018.
 */

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ViewHolder> {

    private Context context;
    private String[] menus;
    private int[] icons;
    private ArrayList<Intent> activities;

    public MainMenuAdapter(Context context, String[] menus, int[] icons, ArrayList<Intent> activities) {
        this.context = context;
        this.menus = menus;
        this.icons = icons;
        this.activities = activities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_menu_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.label.setText(menus[position]);
        holder.icon.setImageResource(icons[position]);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(activities.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return menus.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView label;
        public ImageView icon;
        public CardView cardView;

        public ViewHolder(View v) {
            super(v);
            label = (TextView)v.findViewById(R.id.label);
            icon = (ImageView)v.findViewById(R.id.icon);
            cardView = (CardView)v.findViewById(R.id.cardview);
        }

    }

}
