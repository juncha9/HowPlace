package com.alkemic.howplace.Node;

import android.net.Uri;
import android.util.Log;

import java.util.Date;

public class NaverBlogNode {
    String title;
    String description;
    Uri uri;
    String bloggerName;
    Uri bloggerUri;
    Date postDate;

    public void Logging()
    {
        Log.d("NaverBlog>","Log");
        Log.d("NaverBlog>","-- BlogInformation --");
        Log.d("NaverBlog>","Title:"+title.toString());
        Log.d("NaverBlog>","Description:"+description.toString());
        if(uri != null)
        {
            Log.d("NaverBlog>","URL:"+ uri.toString());
        }
        else
        {
            Log.d("NaverBlog>","URL:");
        }
        Log.d("NaverBlog>","BloggerName:"+bloggerName.toString());
        if(bloggerUri != null)
        {
            Log.d("NaverBlog>","BloggerUrl:"+ bloggerUri.toString());
        }
        else
        {
            Log.d("NaverBlog>","BloggerUrl:");
        }
        if(postDate != null)
        {
            Log.d("NaverBlog>","PostDate:"+postDate.toString());
        }
        else
        {
            Log.d("NaverBlog>","PostDate:");
        }
    }

    private NaverBlogNode(builder builder) {
            this.title = builder.title;
            this.description = builder.description;
            this.uri = builder.uri;
            this.bloggerName = builder.bloggerName;
            this.bloggerUri = builder.bloggerUri;
            this.postDate = builder.postDate;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getBloggerName() {
        return bloggerName;
    }

    public void setBloggerName(String bloggerName) {
        this.bloggerName = bloggerName;
    }

    public Uri getBloggerUri() {
        return bloggerUri;
    }

    public void setBloggerUri(Uri bloggerUri) {
        this.bloggerUri = bloggerUri;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public static final class builder
    {
        String title = "";
        String description = "";
        Uri uri = null;
        String bloggerName = "";
        Uri bloggerUri = null;
        Date postDate = null;

        public builder setTitle(String title) {
            if(title != null)
                this.title = title;
            return this;
        }

        public builder setDescription(String description) {
            if(description != null)
                this.description = description;
            return this;
        }

        public builder setUri(Uri uri) {
            if(uri != null)
                this.uri = uri;
            return this;
        }

        public builder setBloggerName(String bloggerName) {
            if(bloggerName != null)
                this.bloggerName = bloggerName;
            return this;
        }

        public builder setBloggerUri(Uri bloggerUrl) {
            if(bloggerUrl != null)
                this.bloggerUri = bloggerUrl;
            return this;
        }

        public builder setPostDate(Date postDate) {
            if(postDate != null)
                this.postDate = postDate;
            return this;
        }

        public builder(){
        }
        public NaverBlogNode build()
        {
            return new NaverBlogNode(this);
        }
    }


}

