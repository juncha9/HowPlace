package com.alkemic.howplace.Node;

import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MovieNode {
    String title;
    Uri uri;
    List<TimeNode> times;

    public void Logging()
    {
        Log.d("Movie>","-- MovieInformation --");
        Log.d("Movie>","Title:"+title.toString());
        if(uri != null)
            Log.d("Movie>","Uri:"+uri.toString());
        for(TimeNode time : times)
        {
            Log.d("Movie>","TimeStr:"+time.getTimeStr());
            if(time.getUri() != null)
            {
                Log.d("Movie>","TimeUri:"+time.getUri().toString());
            }
        }
    }

    private MovieNode(builder builder) {
            this.title = builder.title;
            this.uri = builder.uri;
            this.times = builder.times;
    }

    public List<TimeNode> getTimes() {
        return times;
    }

    public void setTimes(List<TimeNode> times) {
        this.times = times;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public static final class builder
    {
        String title = "";
        Uri uri = null;
        List<TimeNode> times = new ArrayList<TimeNode>();

        public builder setTitle(String title) {
            if(title != null)
                this.title = title;
            return this;
        }

        public builder setUri(Uri uri) {
            if(uri != null)
                this.uri = uri;
            return this;
        }

        public builder setTimes(List<TimeNode> times) {
            if(times != null)
                this.times = times;
            return this;
        }
        public builder(){
        }
        public MovieNode build()
        {
            return new MovieNode(this);
        }
    }


}

