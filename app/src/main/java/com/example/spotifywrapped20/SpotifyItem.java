package com.example.spotifywrapped20;

public class SpotifyItem {
    private String name;
    private String url;
    private String previewUrl;

    public SpotifyItem(String name, String url, String previewUrl) {
        this.name = name;
        this.url = url;
        this.previewUrl = previewUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
}
