package com.alkemic.howplace.Item;

import com.alkemic.howplace.Node.ArrivalNode;
import com.alkemic.howplace.PlaceType;

import java.util.ArrayList;
import java.util.List;

public class SubwayItem extends Item {

    private List<ArrivalNode> arrivalNodes = new ArrayList<ArrivalNode>();

    public SubwayItem(int id, String name, PlaceType type) {
        super(id, name, type);
    }
    public List<ArrivalNode> getArrivalNodes() {
        return arrivalNodes;
    }

    public SubwayItem setArrivalNodes(List<ArrivalNode> arrivalNodes) {
        this.arrivalNodes = arrivalNodes;
        return this;
    }
}
