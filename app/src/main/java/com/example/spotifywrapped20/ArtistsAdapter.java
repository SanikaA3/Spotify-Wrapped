package com.example.spotifywrapped20;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistsViewHolder> {

    private final List<String> mArtists;

    public ArtistsAdapter(List<String> artists) {
        mArtists = artists;
    }

    @NonNull
    @Override
    public ArtistsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, parent, false);
        return new ArtistsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistsViewHolder holder, int position) {
        String artistName = mArtists.get(position);
        holder.tvArtistName.setText(artistName);
        // Optional: Load artist image if your item layout includes an ImageView
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }

    static class ArtistsViewHolder extends RecyclerView.ViewHolder {
        TextView tvArtistName;
        ImageView ivArtistImage; // Optional

        ArtistsViewHolder(View itemView) {
            super(itemView);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            ivArtistImage = itemView.findViewById(R.id.ivArtistImage); // Optional
        }
    }
}
