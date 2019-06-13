package com.alkemic.howplace.Node;

import android.net.Uri;

public class TimeNode {
    String timeStr;
    Uri uri;

    private TimeNode(builder builder) {
        this.timeStr = builder.timeStr;
        this.uri = builder.uri;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public static final class builder
    {
        String timeStr = "";
        Uri uri = null;
        public builder() {
        }

        public builder setTimeStr(String timeStr) {
            if(timeStr != null)
                this.timeStr = timeStr;
            return this;
        }

        public builder setUri(Uri uri) {
            if(uri != null)
                this.uri = uri;
            return this;
        }
        public TimeNode build()
        {
            return new TimeNode(this);
        }
    }


}
