package com.example.acer.monperabook.CustomAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.acer.monperabook.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Azhary Arliansyah on 31/03/2018.
 */

public class CatatanRecyclerAdapter extends RecyclerView.Adapter<CatatanRecyclerAdapter.CatatanViewHolder> {

    private ArrayList<String> catatan;
    private ArrayList<String> dateTime;
    private int itemLayout;

    public CatatanRecyclerAdapter(ArrayList<String> catatan, ArrayList<String> dateTime, int itemLayout) {
        this.catatan = catatan;
        this.dateTime = dateTime;
        this.itemLayout = itemLayout;
    }

    @Override
    public CatatanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new CatatanViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CatatanViewHolder holder, int position) {
        holder.text.setText(catatan.get(position));
        holder.dateTime.setText(dateTime.get(position));
    }

    @Override
    public int getItemCount() {
        return catatan.size();
    }

    public static class CatatanViewHolder extends RecyclerView.ViewHolder {

        public TextView text;
        public TextView dateTime;

        public CatatanViewHolder(View itemView) {
            super(itemView);
            text = (TextView)itemView.findViewById(R.id.text);
            dateTime = (TextView)itemView.findViewById(R.id.dateTime);
        }
    }

}
