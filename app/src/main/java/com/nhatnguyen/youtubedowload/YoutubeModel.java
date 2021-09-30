package com.nhatnguyen.youtubedowload;

import java.io.Serializable;

public class YoutubeModel implements Serializable {
    public String id, title,youtube_url;

    public YoutubeModel() {
    }


    public YoutubeModel(String id, String title, String youtube_url) {
        this.id = id;
        this.title = title;
        this.youtube_url = youtube_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYoutube_url() {
        return youtube_url;
    }

    public void setYoutube_url(String youtube_url) {
        this.youtube_url = youtube_url;
    }
}
