package com.alkemic.howplace;

import com.alkemic.howplace.Item.Item;

import java.util.Comparator;

public class AscendingDistance implements Comparator<Item>
{
    @Override
    public int compare(Item o1, Item o2) {

        if(o1.getDistance() < o2.getDistance())
        {
            return -1;
        }
        else if(o1 == o2)
        {
            return 0;
        }
        else
        {
            return 1;
        }

    }
}