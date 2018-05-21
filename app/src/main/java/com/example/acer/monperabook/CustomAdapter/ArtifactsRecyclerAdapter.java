package com.example.acer.monperabook.CustomAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.acer.monperabook.ArtifactDetailsActivity;
import com.example.acer.monperabook.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Azhary Arliansyah on 25/03/2018.
 */

public class ArtifactsRecyclerAdapter  extends RecyclerView.Adapter<ArtifactsRecyclerAdapter.ArtifactHolder> {

    private ArrayList<Artifact> artifactsList;
    private Context context;

    public ArtifactsRecyclerAdapter(ArrayList<Artifact> artifactsList, Context context) {
        this.artifactsList = artifactsList;
        this.context = context;
    }

    @Override
    public ArtifactsRecyclerAdapter.ArtifactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.artifact_list, null);
        ArtifactsRecyclerAdapter.ArtifactHolder artifactHolder = new ArtifactsRecyclerAdapter.ArtifactHolder(v);
        return artifactHolder;
    }

    @Override
    public void onBindViewHolder(final ArtifactsRecyclerAdapter.ArtifactHolder holder, int position) {
        final Artifact artifact = artifactsList.get(position);

        holder.artifactTitle.setText(artifact.getTitle());
        holder.artifactLike.setText("Difavoritkan oleh " + artifact.getLike() + " pengunjung");
        try {
            JSONArray photos = new JSONArray(artifact.getImages());
            String URL = "";
            if (photos.length() > 0) {
                URL = context.getString(R.string.server_ip) + "/img/" + photos.getString(0);
            }
            Glide.with(context)
                    .load(URL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            holder.thumbnailProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.thumbnailProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
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

        protected ProgressBar thumbnailProgressBar;
        protected TextView artifactTitle;
        protected TextView artifactLike;
        protected ImageView artifactThumbnail;
        protected CardView cardView;
        protected TextView artifactCategory;

        public ArtifactHolder(View itemView) {
            super(itemView);
            artifactTitle = (TextView) itemView.findViewById(R.id.titles);
            artifactLike = (TextView) itemView.findViewById(R.id.favorite_count);
            artifactCategory = (TextView) itemView.findViewById(R.id.category);
            artifactThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            thumbnailProgressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }

}