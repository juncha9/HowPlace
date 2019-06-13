package com.alkemic.howplace.Item;

import com.alkemic.howplace.Node.MovieNode;
import com.alkemic.howplace.PlaceType;

import java.util.ArrayList;
import java.util.List;

public class TheaterItem extends ShopItem {

    List<MovieNode> movieNodes = new ArrayList<MovieNode>();

    public TheaterItem(int id, String name, PlaceType type) {
        super(id, name, type);
    }

    public List<MovieNode> getMovieNodes() {
        return movieNodes;
    }

    public void setMovieNodes(List<MovieNode> movieNodes) {
        this.movieNodes = movieNodes;
    }

}
