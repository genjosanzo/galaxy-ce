package org.mule.galaxy.index;

import java.io.IOException;

import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyInfo;

public interface Indexer {
    void index(Item item, PropertyInfo property, Index index)
	    throws IOException, IndexException;
}
