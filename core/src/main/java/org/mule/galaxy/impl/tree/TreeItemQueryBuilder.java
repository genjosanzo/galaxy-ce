package org.mule.galaxy.impl.tree;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.mule.galaxy.TreeItem;
import org.mule.galaxy.impl.extension.IdentifiableExtension;
import org.mule.galaxy.impl.extension.IdentifiableExtensionQueryBuilder;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.OpRestriction.Operator;

public class TreeItemQueryBuilder extends IdentifiableExtensionQueryBuilder {

    public TreeItemQueryBuilder(IdentifiableExtension e) throws IntrospectionException {
        super(e);
        
        suffixes.clear();
        suffixes.add("");
        suffixes.add("id");
    }

    @Override
    protected List<String> getMatches(Object o, String property, Operator operator) throws QueryException {
        if (property.endsWith(".id")) {
            if (o instanceof Collection) {
                return new ArrayList<String>((Collection)o);
            } else {
                return Arrays.asList(o.toString());
            }
        }
        
        String path = o.toString();
        
        TreeItem item = ((TreeItemDao)getDao()).getTreeItem(path);
        
        if (item == null) {
            return Collections.emptyList();
        }
        
        return Arrays.asList(item.getId());
    }

}
