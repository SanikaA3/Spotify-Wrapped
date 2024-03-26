package com.example.spotifywrapped20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistsViewHolder> {

    private final List<SpotifyItem> mArtists;
    private Context context;

    public ArtistsAdapter(Context context, List<SpotifyItem> artists) {
        this.context = context;
        this.mArtists = artists;
    }

    @NonNull
    @Override
    public ArtistsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, parent, false);
        return new ArtistsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistsViewHolder holder, int position) {
        SpotifyItem artist = mArtists.get(position);
        holder.tvArtistName.setText(artist.getName());
        Glide.with(context)
                .load(artist.getUrl())
                .into(holder.ivArtistImage);
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }

    static class ArtistsViewHolder extends RecyclerView.ViewHolder {
        TextView tvArtistName;
        ImageView ivArtistImage;

        ArtistsViewHolder(View itemView) {
            super(itemView);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            ivArtistImage = itemView.findViewById(R.id.ivArtistImage);
        }
    }
}
