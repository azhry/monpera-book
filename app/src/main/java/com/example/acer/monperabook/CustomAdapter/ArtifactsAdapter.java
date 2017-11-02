package com.example.acer.monperabook.CustomAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.monperabook.R;

import java.util.List;

/**
 * Created by Azhary Arliansyah on 29/10/2017.
 */

public class ArtifactsAdapter extends ArrayAdapter<Artifact> {

    public ArtifactsAdapter(@NonNull Context context, @NonNull List<Artifact> artifact) {
        super(context, 0, artifact);
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
        String currentTitle = currentArtifact.getTitle();
        String currentDescription = currentArtifact.getDescription();

        TextView artifactTitle = (TextView) listArtifactView.findViewById(R.id.titles);
        TextView artifactDescription = (TextView) listArtifactView.findViewById(R.id.description);
        ImageView artifactThumbnail = (ImageView) listArtifactView.findViewById(R.id.thumbnail);

        artifactTitle.setText(currentTitle);
        artifactDescription.setText(currentDescription);

        return listArtifactView;
    }

}
