package org.mule.galaxy.render;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.Item;

/**
 * Information necessary to display a group of similar artifacts.
 */
public interface ItemRenderer {
    String[] getColumnNames();

    String getColumnValue(Item row, int column);
    
    boolean isSummary(int column);
    
    boolean isDetail(int column);
    
    // Collection<Artifact> sort(Collection<Artifact> artifacts, int column)
}
