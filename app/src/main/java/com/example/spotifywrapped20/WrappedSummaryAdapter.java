package com.example.spotifywrapped20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WrappedSummaryAdapter extends RecyclerView.Adapter<WrappedSummaryAdapter.ViewHolder> {

    private List<WrappedSummary> wrappedSummaries;
    private LayoutInflater inflater;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault());


    public WrappedSummaryAdapter(Context context, List<WrappedSummary> wrappedSummaries) {
        this.inflater = LayoutInflater.from(context);
        this.wrappedSummaries = wrappedSummaries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_wrapped_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WrappedSummary summary = wrappedSummaries.get(position);


        String topTrackText = "Top Track: " + summary.getTopTrack();
        String topArtistText = "Top Artist: " + summary.getTopArtist();
        String timestampText = dateFormat.format(summary.getTimestamp());

        holder.topTrackTextView.setText(topTrackText);
        holder.topArtistTextView.setText(topArtistText);
        holder.timestampTextView.setText(timestampText);
    }



    @Override
    public int getItemCount() {
        return wrappedSummaries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView topTrackTextView, topArtistTextView, timestampTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            topTrackTextView = itemView.findViewById(R.id.topTrackTextView);
            topArtistTextView = itemView.findViewById(R.id.topArtistTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
}
