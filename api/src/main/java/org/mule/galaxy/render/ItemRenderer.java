package org.mule.galaxy.render;

import org.mule.galaxy.Item;

/**
 * Information necessary to display a group of similar artifacts.
 */
public interface ItemRenderer {
    String[] getColumnNames();

    String getColumnValue(Item row, int column);
    
    boolean isSummary(int column);
    
    boolean isDetail(int column);
}
