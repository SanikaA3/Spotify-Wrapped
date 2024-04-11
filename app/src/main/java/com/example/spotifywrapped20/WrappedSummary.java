package com.example.spotifywrapped20;

import java.util.Date;

public class WrappedSummary{
    private String topArtist;
    private String topTrack;
    private Date timestamp;

    public WrappedSummary(){
    }
    public WrappedSummary(String track, String artist, Date timestamp){
        this.topTrack =track;
        this.topArtist = artist;
        this.timestamp = timestamp;
    }

    public String getTopArtist() {
        return topArtist;
    }

    public void setTopArtist(String topArtist) {
        this.topArtist = topArtist;
    }

    public String getTopTrack() {
        return topTrack;
    }

    public void setTopTrack(String topTrack) {
        this.topTrack = topTrack;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}