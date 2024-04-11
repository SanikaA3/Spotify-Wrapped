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

public class TopTracksAdapter extends RecyclerView.Adapter<TopTracksAdapter.TopTracksViewHolder> {

    private List<SpotifyItem> mTracks;
    private Context context;
    public TopTracksAdapter(Context context, List<SpotifyItem> tracks) {
        this.context = context;
        this.mTracks = tracks;
    }

    @NonNull
    @Override
    public TopTracksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new TopTracksViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TopTracksViewHolder holder, int position) {
        SpotifyItem artist = mTracks.get(position);
        holder.tvTrackName.setText(artist.getName());
        Glide.with(context)
                .load(artist.getUrl())
                .into(holder.ivTrackImage);
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    static class TopTracksViewHolder extends RecyclerView.ViewHolder {
        TextView tvTrackName;
        ImageView ivTrackImage;

        TopTracksViewHolder(View itemView) {
            super(itemView);
            tvTrackName = itemView.findViewById(R.id.tvTrackName);
            ivTrackImage = itemView.findViewById(R.id.ivTrackImage);
        }
    }
}
