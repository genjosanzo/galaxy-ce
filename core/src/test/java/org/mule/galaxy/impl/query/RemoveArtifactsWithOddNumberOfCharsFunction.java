package org.mule.galaxy.impl.query;

import java.util.Iterator;
import java.util.Set;

import org.mule.galaxy.Item;
import org.mule.galaxy.query.AbstractFunction;

public class RemoveArtifactsWithOddNumberOfCharsFunction extends AbstractFunction {

    @Override
    public void modifyItems(Object[] args, Set<Item> artifacts) {
        for (Iterator<Item> itr = artifacts.iterator(); itr.hasNext();) {
            Item next = (Item) itr.next();
            
            if (next.getName().length() % 2 == 1) itr.remove();
            else {
                System.out.println("kept " + next.getName());
            }
        }
    }

    @Override
    public String getModule() {
        return "test";
    }

    @Override
    public String getName() {
        return "removeOddChars";
    }

}
