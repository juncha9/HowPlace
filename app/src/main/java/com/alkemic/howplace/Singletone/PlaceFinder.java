package com.alkemic.howplace.Singletone;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.alkemic.howplace.AscendingDistance;
import com.alkemic.howplace.Define;
import com.alkemic.howplace.Item.BusItem;
import com.alkemic.howplace.Item.CafeItem;
import com.alkemic.howplace.Item.Item;
import com.alkemic.howplace.Item.RestaurantItem;
import com.alkemic.howplace.Item.ShopItem;
import com.alkemic.howplace.Item.SubwayItem;
import com.alkemic.howplace.Item.TheaterItem;
import com.alkemic.howplace.Node.ArrivalNode;
import com.alkemic.howplace.PlaceType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.alkemic.howplace.Define.CalculateDistance;
import static java.lang.StrictMath.min;


public class PlaceFinder {
    static {
        System.loadLibrary("keys");
    }

    private static PlaceFinder instance = new PlaceFinder();
    public boolean activated;
    private String key;
    private Locale locale;
    private int radius;
    public boolean isUpdating() {
        return updating;
    }

    private boolean updating = false;
    private List<PlaceFinderEventListener> listeners = new ArrayList<PlaceFinderEventListener>();

    private PlaceFinder()
    {
        activated = false;
        this.key = Define.getWebApiKey();
        this.radius = 400;
    }

    public static synchronized PlaceFinder getInstance() {
        if (instance == null) {
            instance = new PlaceFinder();
        }
        return instance;
    }
    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void AddListener(PlaceFinderEventListener listener) {
            listeners.add(listener);
    }
    public boolean Initialize()
    {
        locale =  Locale.getDefault();
        activated = true;
        return  true;
    }

    public boolean Update() {
        if (LocationChecker.getInstance() == null ) return false;
        if(LocationChecker.getInstance().activated == false) return  false;
        Location location = LocationChecker.getInstance().GetLocation();
        if(location == null) return  false;
        listeners.removeIf(p -> p == null);
        for(PlaceFinderEventListener listener : listeners)
        {
            listener.OnFindStart();
        }
        new UpdateTask(location).execute();
        return  true;
    }

    private class UpdateTask extends AsyncTask<Void,Void,List<Item>>
    {
        Location location;
        OkHttpClient httpClient;

        private UpdateTask(Location location)
        {
            this.location = location;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            httpClient = new OkHttpClient();
            updating = true;
        }

        @Override
        protected List<Item> doInBackground(Void... voids) {
            Log.d("PlaceFinder>Find","Wait finding...");
            List<Item> items = new ArrayList<Item>();
            PlaceType targetPlaceType;
            Request request;
            List<Item> newItems;
            targetPlaceType = PlaceType.restaurant;
            request = getPlaceRequest(location,targetPlaceType,radius);
            newItems = requestPlaces(request,targetPlaceType);
            if(newItems != null && newItems.size() > 0)
            {
                items.addAll(newItems);
            }

            targetPlaceType = PlaceType.cafe;
            request = getPlaceRequest(location,targetPlaceType,radius);
            newItems = requestPlaces(request,targetPlaceType);
            if(newItems != null && newItems.size() > 0)
            {
                items.addAll(newItems);
            }

            targetPlaceType = PlaceType.movie_theater;
            request = getPlaceRequest(location,targetPlaceType,radius);
            newItems = requestPlaces(request,targetPlaceType);
            if(newItems != null && newItems.size() > 0)
            {
                items.addAll(newItems);
            }

            targetPlaceType = PlaceType.subway_station;
            request = getPlaceRequest(location,targetPlaceType,radius);
            newItems = requestPlaces(request,targetPlaceType);
            if(newItems != null && newItems.size() > 0)
            {
                items.addAll(newItems);
            }
            Log.d("PlaceFinder>Find","Find completed (" +items.size()+")");

            Log.d("PlaceFinder>Item","Sort start");
            Collections.sort(items,new AscendingDistance());
            Log.d("PlaceFinder>Item","Sort completed");

            Log.d("PlaceFinder>Item","SetID start");
            int i = 0;
            for(Item item : items )
            {
                item.setId(i++);
            }
            Log.d("PlaceFinder>Item","SetID completed");
            return items;
        }

        @Override
        protected void onPostExecute(List<Item> items) {
            super.onPostExecute(items);
            updating = false;
            listeners.removeIf(p -> p == null);
            for(PlaceFinderEventListener listener : listeners)
            {
                listener.OnFindComplete(items);
            }

        }

        private List<Item> requestPlaces(Request request, PlaceType type)
        {
            List<Item> items = new ArrayList<Item>();
            if(request == null) return items;
            Response response = null;
            String responseText = null;
            try {
                response = httpClient.newCall(request).execute();
                responseText = response.body().string();
                Thread.sleep(10);
            } catch (Exception e) {
                Log.e("PlaceFinder>Find", e.toString());
                return items;
            }
            if (response == null || responseText == null || !response.isSuccessful()) return items;
            Log.d("PlaceFinder>Find", "Response exist: " + response.message());
            JsonParser parser = new JsonParser();
            JsonObject root = (JsonObject) parser.parse(responseText);
            JsonArray jsonArray = (JsonArray) root.get("results");
            if(jsonArray != null && jsonArray.size() > 0)
            {
                int size = jsonArray.size();
                if(size > 10)
                    size = 10;
                for(int i = 0; i<size; i++)
                {
                    JsonObject jsonItem = jsonArray.get(i).getAsJsonObject();
                    if(jsonItem != null)
                    {
                        Item item = parsePlaceJsonItem(jsonItem,type);
                        if(item.getLatLng() != null)
                        {
                            item.setDistance(Define.CalculateDistance(new LatLng(location.getLatitude(),location.getLongitude()), item.getLatLng()));
                        }
                        items.add(item);
                    }

                }
            }
            return items;
        }

        private Item parsePlaceJsonItem(JsonObject jsonItem, PlaceType type)
        {
            if(jsonItem == null) return  null;
            JsonObject location = jsonItem.get("geometry").getAsJsonObject().get("location").getAsJsonObject();
            double lat = location.get("lat").getAsDouble();
            double lng = location.get("lng").getAsDouble();
            LatLng latLng = new LatLng(lat,lng);
            String placeId = jsonItem.get("place_id").getAsString();
            String name = jsonItem.get("name").getAsString();
            JsonArray photos = null;
            PhotoMetadata metadata = null;
            if(jsonItem.get("photos") != null)
            {
                photos = jsonItem.get("photos").getAsJsonArray();
                if(photos != null && photos.size()>0)
                {
                    JsonObject photoObject = photos.get(0).getAsJsonObject();
                    String photo_reference = photoObject.get("photo_reference").getAsString();
                    String attributions = photoObject.get("html_attributions").getAsJsonArray().get(0).getAsString();
                    int height = photoObject.get("height").getAsInt();
                    int width = photoObject.get("width").getAsInt();
                    metadata = PhotoMetadata.builder(photo_reference)
                            .setAttributions(attributions)
                            .setWidth(width)
                            .setHeight(height)
                            .build();
                }

            }
            String address = jsonItem.get("vicinity").getAsString();
            boolean open_now = false;
            if(jsonItem.get("opening_hours") != null)
                open_now = jsonItem.get("opening_hours").getAsJsonObject().get("open_now").getAsBoolean();
            int price_level = 0;
            if(jsonItem.get("price_level") != null)
                price_level = jsonItem.get("price_level").getAsInt();
            float rating = 0;
            if(jsonItem.get("rating") != null)
                rating = jsonItem.get("rating").getAsFloat();
            Item item = null;
            switch(type)
            {
                case none:
                    item = new Item(-1, name, type);
                    break;
                case restaurant:
                    item = new RestaurantItem(-1,name,type);
                    break;
                case cafe:
                    item = new CafeItem(-1,name,type);
                    break;
                case movie_theater:
                    item = new TheaterItem(-1,name,type);
                    break;
                case subway_station:
                    item = new SubwayItem(-1,name,type);
                    break;
                case bus_station:
                    item = new BusItem(-1,name,type);
                    break;
            }
            if(item != null)
            {
                item.setPlaceID(placeId);
                item.setLatLng(latLng);
                item.setPhotoMetadata(metadata);
                item.setAddress(address);
                item.setOpen(open_now);
            }
            if(item instanceof ShopItem)
            {
                ShopItem shopItem = (ShopItem) item;
                shopItem.setPriceLevel(price_level);
                shopItem.setRating(rating);
            }
            return item;
        }

        private Request getPlaceRequest(Location location, PlaceType type, int radius)
        {
            Request request;
            String prefix = "https://maps.googleapis.com/maps/api/place/nearbysearch";
            String docType = "json";
            String key = "key=" + Define.getWebApiKey();
            String locationReq = "location="+location.getLatitude()+","+location.getLongitude();
            String radiusReq = "radius="+radius;
            String typeReq = "type="+type.toString();
            String language = "language=" + locale;
            String url = prefix + "/" + docType +"?" + locationReq + "&" + radiusReq + "&" +typeReq + "&"+ language +"&" + key;
            Log.d("PlaceFinder>"+type.toString(), "Place request:"+url);
            request = new Request.Builder()
                    .url(url)
                    .build();
            return  request;
        }

    }

}


