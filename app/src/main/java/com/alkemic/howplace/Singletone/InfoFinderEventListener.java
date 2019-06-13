package com.alkemic.howplace.Singletone;

import com.alkemic.howplace.Item.Item;
import com.alkemic.howplace.Item.ShopItem;
import com.alkemic.howplace.Item.SubwayItem;
import com.alkemic.howplace.Item.TheaterItem;

public interface InfoFinderEventListener {

    void OnImageUpdateStart(Item item);
    void OnSubwayUpdateStart(SubwayItem item);
    void OnShopUpdateStart(ShopItem item);
    void OnBlogUpdateStart(ShopItem item);
    void OnTheaterUpdateStart(TheaterItem item);

    void OnImageUpdateEnd(Item item);
    void OnSubwayUpdateEnd(SubwayItem item);
    void OnShopUpdateEnd(ShopItem item);
    void OnBlogUpdateEnd(ShopItem item);
    void OnTheaterUpdateEnd(TheaterItem item);

}
