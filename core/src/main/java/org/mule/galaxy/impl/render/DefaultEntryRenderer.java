package org.mule.galaxy.impl.render;

import org.mule.galaxy.Item;
import org.mule.galaxy.render.ItemRenderer;

public class DefaultEntryRenderer implements ItemRenderer {

    public String[] getColumnNames() {
        return new String [] {
          "Name",
          "Path"
        };
    }

    public String getColumnValue(Item row, int i) {
        switch (i) {
        case 0:
            return row.getName();
        case 1:
            return row.getParent().getPath();
        }
        
        return null;
    }

    public boolean isSummary(int column) {
        return column != 1;
    }

    public boolean isDetail(int column) {
        return true;
    }
    
}
