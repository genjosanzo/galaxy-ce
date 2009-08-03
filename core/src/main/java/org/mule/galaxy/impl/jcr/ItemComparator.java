package org.mule.galaxy.impl.jcr;

import java.util.Comparator;

import org.mule.galaxy.Item;

public class ItemComparator implements Comparator<Item> {

    public int compare(Item w1, Item w2) {
        return w1.getName().compareTo(w2.getName());
    }

}
