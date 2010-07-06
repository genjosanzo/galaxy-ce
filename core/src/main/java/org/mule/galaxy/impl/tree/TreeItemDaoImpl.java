package org.mule.galaxy.impl.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.jackrabbit.util.ISO9075;
import org.mule.galaxy.TreeItem;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;

public class TreeItemDaoImpl extends AbstractReflectionDao<TreeItem> implements TreeItemDao {

    public TreeItemDaoImpl() throws Exception {
        super(TreeItem.class, "treeItems", true);
    }

    public List<TreeItem> getRootTreeItems() {
        return find("parent", null);
    }

    @Override
    public TreeItem build(Node node, Session session) throws Exception {
        TreeItem ti = super.build(node, session);
        
        String parentId = JcrUtil.getStringOrNull(node, "parent");
        
        if (parentId != null) {
            ti.setParent(build(session.getNodeByUUID(parentId), session));
        }
        return ti;
    }

    public TreeItem getTreeItem(String path) {
        final String[] paths = path.split("/");

        final Map<String,Object> criteria = new HashMap<String,Object>();
        criteria.put("name", paths[0]);
        criteria.put("parent", null);

        final List<TreeItem> results = find(criteria);
        if (results.size() == 0) {
            return null;
        }

        TreeItem item = results.get(0);
        for (int i = 1; i < paths.length; i++) {
            item = item.getChild(paths[i]);
         
            if (item == null) {
                return null;
            }
        }
        
        return item;
    }

    @Override
    protected String generateNodeName(TreeItem t) {
        return ISO9075.encode(t.getName());
    }

    @Override
    protected String getObjectNodeName(TreeItem t) {
        return t.getName();
    }

}
