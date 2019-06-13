package com.alkemic.howplace;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.lang.StrictMath.acos;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;

public class Define {

    public static double CalculateDistance(LatLng latLng1, LatLng latLng2)
    {
        double dLat1 = latLng1.latitude;
        double dLon1 = latLng1.longitude;
        double dLat2 = latLng2.latitude;
        double dLon2 = latLng2.longitude;
        return acos(sin(dLat1)*sin(dLat2) + cos(dLat1)*cos(dLat2)*cos(dLon1 - dLon2));
    }
    public static synchronized native String getWebApiKey();
    public static synchronized native String getAppApiKey();
    public static synchronized native String getNaverClientID();
    public static synchronized native String getNaverClientSecret();
    public static synchronized native String getSubwayKey();

    public static String PlaceTypeToString(PlaceType type)
    {
        String text = "";
        switch (type)
        {
            case restaurant:
                text = "식당";
                break;
            case cafe:
                text = "카페";
                break;
            case bus_station:
                text = "버스";
                break;
            case subway_station:
                text ="지하철";
                break;
            case movie_theater:
                text = "영화관";
                break;
            case none:
                text = "미분류";
                break;
        }
        return text;
    }

    public static String encodeURI(String original)
    {
        try
        {
            //return URLEncoder.encodeURI(original, "utf-8");
            //fixed: to comply with RFC-3986
            return URLEncoder.encode(original, "euc-kr").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        }
        catch(UnsupportedEncodingException e)
        {
        }
        return null;
    }


}
