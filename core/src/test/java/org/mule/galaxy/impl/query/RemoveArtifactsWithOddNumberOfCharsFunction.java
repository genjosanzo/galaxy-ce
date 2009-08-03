package org.mule.galaxy.impl.query;

import org.mule.galaxy.Item;
import org.mule.galaxy.query.AbstractFunction;

public class RemoveArtifactsWithOddNumberOfCharsFunction extends AbstractFunction {

    @Override
    public boolean filter(Object[] args, Item next) {
        if (next.getName().length() % 2 == 1) {
            return true;
        } else {
            System.out.println("kept " + next.getName());
            return false;
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
