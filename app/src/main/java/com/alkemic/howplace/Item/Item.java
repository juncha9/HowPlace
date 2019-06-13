package com.alkemic.howplace.Item;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.alkemic.howplace.PlaceType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;

public class Item {
    protected int id;
    protected String name;
    protected PlaceType type;
    protected String placeID = "";
    protected LatLng latLng = null;
    protected String address = "";
    protected PhotoMetadata photoMetadata = null;
    protected Place place = null;
    protected Bitmap thumbnail = null;
    protected boolean isOpen = false;
    protected double distance = 0;
    protected boolean updated;


    public Item(int id, String name, PlaceType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
    public String getPlaceID() {
        return placeID;
    }
    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlaceType getType() {
        return type;
    }

    public void setType(PlaceType type) {
        this.type = type;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public LatLng getLatLng() {
        return latLng;
    }
    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public PhotoMetadata getPhotoMetadata() {
        return photoMetadata;
    }
    public void setPhotoMetadata(PhotoMetadata photoMetadata) {
        this.photoMetadata = photoMetadata;
    }
    public boolean isOpen() {
        return isOpen;
    }
    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isUpdated() {
        return updated;
    }
    public void setUpdated(boolean updated) {
        this.updated = updated;
    }


}
