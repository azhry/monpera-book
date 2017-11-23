package com.example.acer.monperabook.CustomAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.acer.monperabook.AsyncTask.DownloadImageTask;
import com.example.acer.monperabook.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azhary Arliansyah on 29/10/2017.
 */

public class ArtifactsAdapter extends ArrayAdapter<Artifact> {

    private List<Artifact> mArtifact;
    private ArrayList<Artifact> mArtifactList;
    private Context mContext;

    public ArtifactsAdapter(@NonNull Context context, @NonNull List<Artifact> artifact) {
        super(context, 0, artifact);
        this.mContext = context;
        this.mArtifact = artifact;
        this.mArtifactList = new ArrayList<>();
        this.mArtifactList.addAll(artifact);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listArtifactView = convertView;
        if (listArtifactView == null) {
            listArtifactView = LayoutInflater.from(getContext()).inflate(
                    R.layout.artifact_list, parent, false);
        }

        Artifact currentArtifact = getItem(position);
        String currentCode = currentArtifact.getCode();
        String currentTitle = currentArtifact.getTitle();
        String currentDescription = currentArtifact.getDescription();

        TextView artifactTitle = (TextView) listArtifactView.findViewById(R.id.titles);
        TextView artifactDescription = (TextView) listArtifactView.findViewById(R.id.description);
        ImageView artifactThumbnail = (ImageView) listArtifactView.findViewById(R.id.thumbnail);
        final ProgressBar thumbnailProgressBar = (ProgressBar)listArtifactView.findViewById(R.id.progress);

        String URL = getContext().getString(R.string.server_ip) + "/img/" + currentCode + ".jpg";
        Glide.with(this.mContext)
                .load(URL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        thumbnailProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        thumbnailProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(artifactThumbnail);
//        new DownloadImageTask(artifactThumbnail).execute(URL);

        artifactTitle.setText(currentTitle);
        artifactDescription.setText(currentDescription);

        return listArtifactView;
    }

    public void filter(String query) {
        query = query.toLowerCase();
        mArtifact.clear();
        if (query.length() <= 0) {
            mArtifact.addAll(mArtifactList);
        } else {
            for (Artifact artifact : mArtifactList) {
                if (artifact.getTitle().toLowerCase().contains(query)) {
                    mArtifact.add(artifact);
                }
            }
        }

        notifyDataSetChanged();
    }

}
