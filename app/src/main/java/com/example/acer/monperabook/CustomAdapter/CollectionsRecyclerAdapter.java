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

import com.bumptech.glide.Glide;
import com.example.acer.monperabook.ArtifactDetailsActivity;
import com.example.acer.monperabook.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Azhary Arliansyah on 24/02/2018.
 */

public class CollectionsRecyclerAdapter extends RecyclerView.Adapter<CollectionsRecyclerAdapter.ArtifactHolder> {

    private ArrayList<Artifact> artifactsList;
    private Context context;

    public CollectionsRecyclerAdapter(ArrayList<Artifact> artifactsList, Context context) {
        this.artifactsList = artifactsList;
        this.context = context;
    }

    @Override
    public ArtifactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewpager, null);
        ArtifactHolder artifactHolder = new ArtifactHolder(v);
        return artifactHolder;
    }

    @Override
    public void onBindViewHolder(ArtifactHolder holder, int position) {
        final Artifact artifact = artifactsList.get(position);
        holder.artifactTitle.setText(artifact.getTitle());
        holder.artifactLike.setText(artifact.getLike());
        JSONArray photos = null;
        try {
            photos = new JSONArray(artifact.getImages());
            String URL = "";
            if (photos.length() > 0) {
                URL = context.getString(R.string.server_ip) + "/img/" + photos.getString(0);
            }
            Glide.with(context)
                    .load(URL)
                    .into(holder.artifactThumbnail);
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent artifactDetailsIntent = new Intent(view.getContext(), ArtifactDetailsActivity.class);
//
//                artifactDetailsIntent.putExtra("kode_artifak", artifact.getCode());
//                artifactDetailsIntent.putExtra("nama", artifact.getTitle());
//                artifactDetailsIntent.putExtra("deskripsi", artifact.getDescription());
//                artifactDetailsIntent.putExtra("foto", artifact.getImages());
//                artifactDetailsIntent.putExtra("like", artifact.getLike());
//                artifactDetailsIntent.putExtra("kategori", artifact.getCategory());
//
//                view.getContext().startActivity(artifactDetailsIntent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return artifactsList.size();
    }

    public static class ArtifactHolder extends RecyclerView.ViewHolder {

        protected TextView artifactTitle;
        protected TextView artifactLike;
        protected ImageView artifactThumbnail;
        protected CardView cardView;

        public ArtifactHolder(View itemView) {
            super(itemView);
            artifactTitle = (TextView) itemView.findViewById(R.id.title);
            artifactLike = (TextView) itemView.findViewById(R.id.favorite_count);
            artifactThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }

}
