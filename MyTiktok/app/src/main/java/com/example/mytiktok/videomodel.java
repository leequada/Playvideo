package com.example.mytiktok;

import java.io.Serializable;

public class videomodel implements Serializable {
    String id,Title,timeslap,VideoUri;


    public videomodel(){

    }
    public videomodel(String id, String title, String timeslap, String videoUri) {
        this.id = id;
        Title = title;
        this.timeslap = timeslap;
        VideoUri = videoUri;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public String getTimeslap() {
        return timeslap;
    }

    public String getVideoUri() {
        return VideoUri;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setTimeslap(String timeslap) {
        this.timeslap = timeslap;
    }

    public void setVideoUri(String videoUri) {
        VideoUri = videoUri;
    }
}
