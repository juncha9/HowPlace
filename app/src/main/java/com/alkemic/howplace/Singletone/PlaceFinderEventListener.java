package com.alkemic.howplace.Singletone;

import com.alkemic.howplace.Item.Item;

import java.util.List;

public interface PlaceFinderEventListener
{
    void OnFindStart();
    void OnFindComplete(List<Item> newItems);
}