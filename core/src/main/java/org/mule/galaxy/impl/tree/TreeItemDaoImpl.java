package org.mule.galaxy.impl.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.TreeItem;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;

public class TreeItemDaoImpl extends AbstractReflectionDao<TreeItem> implements TreeItemDao {

    public TreeItemDaoImpl() throws Exception {
        super(TreeItem.class, "treeItems", true);
    }

    public List<TreeItem> getRootTreeItems() {
        return find("parent", null);
    }

    public TreeItem getTreeItem(String path) {
        String[] paths = path.split("/");
        
        Map<String,Object> criteria = new HashMap<String,Object>();
        criteria.put("name", paths[0]);
        criteria.put("parent", null);
        
        List results = find(criteria);
        if (results.size() == 0) {
            return null;
        }
        
        TreeItem item = (TreeItem) results.get(0);
        for (int i = 1; i < paths.length; i++) {
            item = item.getChild(paths[i]);
         
            if (item == null) {
                return null;
            }
        }
        
        return item;
    }
    
}
