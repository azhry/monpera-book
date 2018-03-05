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
import com.example.acer.monperabook.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azhary Arliansyah on 24/02/2018.
 */

public class CollectionsAdapter extends ArrayAdapter<Artifact> {

    private List<Artifact> mArtifact;
    private ArrayList<Artifact> mArtifactList;
    private Context mContext;

    public CollectionsAdapter(@NonNull Context context, @NonNull List<Artifact> artifact) {
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
                    R.layout.item_viewpager, parent, false);
        }

        Artifact currentArtifact = getItem(position);
        String currentCode = currentArtifact.getCode();
        String currentTitle = currentArtifact.getTitle();
        String currentDescription = currentArtifact.getDescription();
        String currentLike = currentArtifact.getLike();

        TextView artifactTitle = (TextView) listArtifactView.findViewById(R.id.title);
//        TextView artifactDescription = (TextView) listArtifactView.findViewById(R.id.description);
        TextView artifactLike = (TextView) listArtifactView.findViewById(R.id.favorite_count);
        ImageView artifactThumbnail = (ImageView) listArtifactView.findViewById(R.id.thumbnail);
//        final ProgressBar thumbnailProgressBar = (ProgressBar)listArtifactView.findViewById(R.id.progress);

        String URL = getContext().getString(R.string.server_ip) + "/img/" + currentCode + ".jpg";
        Glide.with(this.mContext)
                .load(URL)
                .into(artifactThumbnail);

        artifactTitle.setText(currentTitle);
//        artifactDescription.setText(currentDescription);
        artifactLike.setText(currentLike);

        return listArtifactView;
    }

}
