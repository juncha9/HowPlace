package com.alkemic.howplace.Item;

import android.net.Uri;

import com.alkemic.howplace.Node.NaverBlogNode;
import com.alkemic.howplace.PlaceType;

import java.util.ArrayList;
import java.util.List;

public class ShopItem extends Item {

    protected int priceLevel = 0;
    protected float rating = 0;
    protected String telNumber = "";
    protected Uri uri = null;
    protected List<NaverBlogNode> naverBlogs = new ArrayList<NaverBlogNode>();


    public ShopItem(int id, String name, PlaceType type)
    {
        super(id, name, type);
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public List<NaverBlogNode> getNaverBlogs() {
        return naverBlogs;
    }

    public void setNaverBlogs(List<NaverBlogNode> naverBlogs) {
        this.naverBlogs = naverBlogs;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public ShopItem setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
        return this;
    }

    public float getRating() {
        return rating;
    }

    public ShopItem setRating(float rating) {
        this.rating = rating;
        return this;
    }


}
