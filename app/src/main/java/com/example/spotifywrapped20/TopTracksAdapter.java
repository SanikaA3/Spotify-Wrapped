package com.example.spotifywrapped20;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TopTracksAdapter extends RecyclerView.Adapter<TopTracksAdapter.TopTracksViewHolder> {

    private List<String> mTracks; // Assuming we're just dealing with track names for simplicity

    public TopTracksAdapter(List<String> tracks) {
        mTracks = tracks;
    }

    @NonNull
    @Override
    public TopTracksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new TopTracksViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TopTracksViewHolder holder, int position) {
        String trackName = mTracks.get(position);
        holder.tvTrackName.setText(trackName);
        // Here you'd also load the track image into ivTrackImage
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
