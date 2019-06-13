package com.alkemic.howplace.Singletone;

import com.alkemic.howplace.Item.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemManager
{

    private static ItemManager instance = new ItemManager();
    public boolean activated;
    public List<Item> items = new ArrayList<Item>();
    private List<ItemManagerEventListener> listeners = new ArrayList<ItemManagerEventListener>();
    private PlaceFinderEventListener placeFinderEventListener = new PlaceFinderEventListener() {
        @Override
        public void OnFindStart() {
            items.clear();
        }

        @Override
        public void OnFindComplete(List<Item> newItems) {
            if(newItems.size() <= 0)
            {
                listeners.removeIf(p -> p == null);
                for(ItemManagerEventListener listener : listeners)
                {
                    listener.OnItemEmpty();
                }
            }
            items.addAll(newItems);
            listeners.removeIf(p -> p == null);
            for(ItemManagerEventListener listener : listeners)
            {
                listener.OnItemCreate();
            }
            InfoFinder.getInstance().Update(items);
        }
    };

    private ItemManager()
    {
        activated = false;
        items = new ArrayList<Item>();
    }

    public static synchronized ItemManager getInstance()
    {
        if(instance == null)
        {
            instance = new ItemManager();
        }
        return  instance;
    }

    public boolean Initialize()
    {
        PlaceFinder.getInstance().AddListener(placeFinderEventListener);
        activated = true;
        return  true;
    }

    public void AddListener(ItemManagerEventListener listener) {
        listeners.add(listener);
    }
}
